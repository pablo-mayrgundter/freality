const shapefile = require('shapefile');

function init() {
  let first = true;
  const filename = process.argv[2];
  const zipPrefix = process.argv[3] || '787';
  shapefile.open(filename)
    .then(source => source.read()
          .then(function log(result) {
              if (result.done) {
                console.log('  </g>\n</svg>');
                return;
              };
              if (first) {
                const bb = source.bbox;
                const bbs = `${-bb[2]} ${-bb[3]}`;
                const bbXOff = (bb[2] - bb[0]);
                console.error(source);
                console.log(
`<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg id="78701" xmlns="http://www.w3.org/2000/svg"
  width="1000" height="1000" viewBox="0 0 1000 1000">
  <g transform="scale(1000 1000) translate(${bbXOff} 0) scale(1.0 -1.0) translate(${bbs})"
     fill="lightblue" stroke="#000000" stroke-width="0.001">`);
                first = false;
              }
              if (result.value.properties.GEOID10.startsWith(zipPrefix)) {
                console.log('<path d="M'
                            + result.value.geometry.coordinates[0].join(' L').replace(/,/g, ' ')
                            +' Z"/>');
                return source.read().then(log);
              }
            }))
    .catch(error => console.error(error.stack));
}
init();
