function percentageInRange(v, min, max) {
    return max === min || v < min ? 0 : (v - min) / (max - min);
}

export {percentageInRange};
