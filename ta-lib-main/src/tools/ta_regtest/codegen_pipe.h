#ifndef CODEGEN_PIPE_H
#define CODEGEN_PIPE_H

#include "ta_error_number.h"

/* Opaque handle for the ta_codegen subprocess */
typedef struct {
    int to_child_fd;    /* fd to write JSON-RPC requests */
    int from_child_fd;  /* fd to read JSON-RPC responses */
    int child_pid;      /* pid of ta_codegen process */
} CodegenPipe;

/* Start a JSON-RPC subprocess.
 * argv: NULL-terminated array of strings (program + args).
 *   e.g. {"./ta_codegen", "serve", NULL}
 *   e.g. {"java", "-cp", "ta_codegen_java", "TaCodegenServe", NULL}
 * Returns TA_TEST_PASS on success, error code on failure.
 */
ErrorNumber codegen_pipe_open(CodegenPipe *cp, const char *const argv[]);

/* Send a JSON-RPC request line and read the response line.
 * request: null-terminated JSON string (newline will be appended)
 * response: buffer to receive response (caller allocates)
 * response_size: size of response buffer
 * Returns TA_TEST_PASS on success, error code on failure.
 */
ErrorNumber codegen_pipe_call(CodegenPipe *cp,
                              const char *request,
                              char *response,
                              int response_size);

/* Stop the ta_codegen subprocess and clean up.
 * Always safe to call (handles already-closed state).
 */
void codegen_pipe_close(CodegenPipe *cp);

#endif
