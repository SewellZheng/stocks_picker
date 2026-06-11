double ta_true_range(double th, double tl, double yc) {
    double range = th - tl;
    double tmp = fabs(th - yc);
    if (tmp > range) range = tmp;
    tmp = fabs(tl - yc);
    if (tmp > range) range = tmp;
    return range;
}
