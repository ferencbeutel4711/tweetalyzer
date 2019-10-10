import {percentageInRange} from "./math";

function colorFromRedToGreen(value, min, max) {
    const r = percentageInRange(value, min, max) * 255;
    const g = 255 - percentageInRange(value, min, max) * 255;
    const b = 0;

    return `rgb(${r},${g},${b})`;
}

function colorFromBlueToYellow(value, min, max) {
    const r = 255 - percentageInRange(value, min, max) * 255;
    const g = 255 - percentageInRange(value, min, max) * 255;
    const b = percentageInRange(value, min, max) * 255;

    return `rgb(${r},${g},${b})`;
}

export {colorFromRedToGreen, colorFromBlueToYellow};
