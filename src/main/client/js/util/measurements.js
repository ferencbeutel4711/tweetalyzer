function getMeasurementNode(fontSize) {
  const measurementNode = document.createElement('span');
  measurementNode.style.fontSize = `${fontSize}px`;
  measurementNode.style.display = 'inline-block';
  measurementNode.style.position = 'absolute';
  measurementNode.style.left = '-10000px';

  return measurementNode;
}

export {getMeasurementNode};
