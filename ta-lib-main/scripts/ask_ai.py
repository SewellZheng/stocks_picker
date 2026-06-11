#!/usr/bin/env python3
"""Ask AI for performance/optimization advice with automatic escalation.

Sends your question to Gemini 3.1 Flash Lite first (fast, cheap).
Flash Lite answers AND recommends whether to escalate to a deeper model.
If escalation is recommended, automatically follows up.

Usage:
    python3 scripts/ask_ai.py "Why does CDL3BLACKCROWS run 1.16x slower?"
    python3 scripts/ask_ai.py --auto-escalate "Complex question that might need Pro"
    python3 scripts/ask_ai.py --model gemini-pro "Skip triage, go straight to Pro"
    python3 scripts/ask_ai.py --model gpt "Second opinion on icache layout effects"

Models available:
    gemini-flash-lite  (default first pass -- fast, cheap, good enough for most)
    gemini-pro         (deep reasoning, architecture decisions)
    gpt                (second opinion, different perspective)

Requires GEMINI_API_KEY and/or OPENAI_API_KEY in .env
"""
import argparse
import base64
import json
import os
import subprocess
import sys
from pathlib import Path


def load_env():
    env_path = Path(__file__).parent.parent / ".env"
    if env_path.exists():
        for line in env_path.read_text().splitlines():
            line = line.strip()
            if line and not line.startswith("#") and "=" in line:
                key, val = line.split("=", 1)
                os.environ.setdefault(key.strip(), val.strip())


def ask_gemini(prompt: str, model: str = "gemini-3.1-flash-lite-preview",
               image_path: str | None = None, thinking: bool = False) -> str:
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        return "[error] GEMINI_API_KEY not set"

    parts = []

    if image_path and os.path.exists(image_path):
        with open(image_path, "rb") as f:
            img_data = base64.b64encode(f.read()).decode()
        mime = "image/png" if image_path.endswith(".png") else "image/jpeg"
        parts.append({"inline_data": {"mime_type": mime, "data": img_data}})

    parts.append({"text": prompt})

    body: dict = {"contents": [{"parts": parts}]}
    if thinking:
        body["generationConfig"] = {
            "thinkingConfig": {"thinkingLevel": "HIGH"},
            "maxOutputTokens": 8000,
        }
    else:
        body["generationConfig"] = {"maxOutputTokens": 4000}

    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={api_key}"
    result = subprocess.run(
        ["curl", "-s", url, "-H", "Content-Type: application/json",
         "-d", json.dumps(body)],
        capture_output=True, text=True, timeout=120,
    )
    try:
        r = json.loads(result.stdout)
        parts = r.get("candidates", [{}])[0].get("content", {}).get("parts", [])
        return "\n".join(p["text"] for p in parts if "text" in p)
    except (json.JSONDecodeError, KeyError, IndexError):
        return f"[error] {result.stdout[:500]}"


def ask_gpt(prompt: str, model: str = "gpt-4.1-mini",
            image_path: str | None = None) -> str:
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        return "[error] OPENAI_API_KEY not set"

    content: list | str
    if image_path and os.path.exists(image_path):
        with open(image_path, "rb") as f:
            img_data = base64.b64encode(f.read()).decode()
        mime = "image/png" if image_path.endswith(".png") else "image/jpeg"
        content = [
            {"type": "image_url", "image_url": {"url": f"data:{mime};base64,{img_data}"}},
            {"type": "text", "text": prompt},
        ]
    else:
        content = prompt

    body = {
        "model": model,
        "messages": [{"role": "user", "content": content}],
        "max_tokens": 4000,
    }
    result = subprocess.run(
        ["curl", "-s", "https://api.openai.com/v1/chat/completions",
         "-H", f"Authorization: Bearer {api_key}",
         "-H", "Content-Type: application/json",
         "-d", json.dumps(body)],
        capture_output=True, text=True, timeout=120,
    )
    try:
        r = json.loads(result.stdout)
        return r["choices"][0]["message"]["content"]
    except (json.JSONDecodeError, KeyError, IndexError):
        return f"[error] {result.stdout[:500]}"


TRIAGE_SUFFIX = """

---
After your answer, add a JSON block on its own line:
```json
{"escalate": true/false, "model": "gemini-pro" or "gpt", "reason": "why or why not"}
```
Set escalate=true only if the question involves deep microarchitectural analysis, ARM64 pipeline behavior, compiler optimization theory, or you're unsure about your answer. For straightforward questions, set escalate=false.
"""


def parse_escalation(text: str) -> dict | None:
    for line in reversed(text.splitlines()):
        line = line.strip().strip("`")
        if line.startswith("{") and "escalate" in line:
            try:
                return json.loads(line)
            except json.JSONDecodeError:
                pass
    return None


def main():
    parser = argparse.ArgumentParser(description="Ask AI with auto-escalation")
    parser.add_argument("question", nargs="+", help="Your question")
    parser.add_argument("--image", "-i", help="Path to screenshot/image")
    parser.add_argument("--model", "-m", help="Skip triage, go straight to this model (gemini-pro, gpt)")
    parser.add_argument("--auto-escalate", action="store_true", help="Auto-escalate if Flash Lite recommends it")
    args = parser.parse_args()

    load_env()
    question = " ".join(args.question)

    # Direct model override -- skip triage
    if args.model:
        if args.model == "gemini-pro":
            print("[asking gemini-3.1-pro-preview directly]")
            print(ask_gemini(question, "gemini-3.1-pro-preview", args.image, thinking=True))
        elif args.model == "gpt":
            print("[asking gpt-4.1-mini directly]")
            print(ask_gpt(question, image_path=args.image))
        else:
            print(f"[asking {args.model} directly]")
            print(ask_gemini(question, args.model, args.image))
        return

    # Step 1: Ask Flash Lite (fast triage)
    triage_prompt = question + TRIAGE_SUFFIX
    print("[1/2] asking gemini-3.1-flash-lite-preview...")
    response = ask_gemini(triage_prompt, "gemini-3.1-flash-lite-preview", args.image)
    print(response)
    print()

    if not args.auto_escalate:
        return

    # Step 2: Check escalation recommendation
    esc = parse_escalation(response)
    if not esc or not esc.get("escalate"):
        print("[done -- flash-lite was sufficient]")
        return

    model = esc.get("model", "gemini-pro")
    reason = esc.get("reason", "")
    print(f"[2/2] escalating to {model} -- {reason}")
    print()

    followup = f"A fast model gave this initial answer to the question: \"{question}\"\n\nInitial answer:\n{response}\n\nPlease provide a deeper, more thorough analysis. Focus on what the initial answer might have missed or gotten wrong."

    if model == "gpt":
        print(ask_gpt(followup, image_path=args.image))
    elif model == "gemini-pro":
        print(ask_gemini(followup, "gemini-3.1-pro-preview", args.image, thinking=True))
    else:
        print(ask_gemini(followup, model, args.image))


if __name__ == "__main__":
    main()
