import React, {useEffect, useState} from 'react'
import {useParams} from 'react-router'
import {MathJaxContext} from 'better-react-mathjax'


export default function Drake() {


  const modelConfig = {
    num: {
      a: 'N', v: '', l: '\\(N\\)',
      s: 'the number of civilizations in our galaxy with which communication might be possible (i.e. which are on our current past light cone)',
      readonly: true,
    },
    eq: {a: '=', symbol: true},
    starFormRate: {
      a: 'Rs', v: 7, l: '\\(R_{*}\\)',
      s: 'the average rate of star formation in our galaxy per year',
      c: 'https://www.google.com/search?q=how+many+stars+form+per+year+milky+way'
    },
    m0: {a: '\\(\\cdot\\)', symbol: true},
    fracWithPlanet: {
      a: 'Fp', v: 1, l: '\\(f_p\\)',
      s: 'the fraction of those stars that have planets'
    },
    m1: {a: '\\(\\cdot\\)', symbol: true},
    rateSupportLife: {
      a: 'Ne', v: 4, l: '\\(n_e\\)',
      s: 'the average number of planets that can potentially support life per star that has planets',
    },
    m2: {a: '\\(\\cdot\\)', symbol: true},
    fracLife: {
      a: 'Fl', v: 1, l: '\\(f_l\\)',
      s: 'the fraction of planets that could support life that actually develop life at some point',
    },
    m3: {a: '\\(\\cdot\\)', symbol: true},
    fracIntel: {
      a: 'Fi', v: 0.0002, l: '\\(f_i\\)',
      s: 'the fraction of planets with life that actually go on to develop intelligent life (civilizations)',
    },
    m4: {a: '\\(\\cdot\\)', symbol: true},
    fracCiv: {
      a: 'Fc', v: 0.5, l: '\\(f_c\\)',
      s: 'the fraction of civilizations that develop a technology that releases detectable signs of their existence into space',
    },
    m5: {a: '\\(\\cdot\\)', symbol: true},
    civDuration: {
      a: 'L', v: 10000, l: '\\(L\\)',
      s: 'the length of time for which such civilizations release detectable signals into space',
    },
  }


  const [model, setModel] = useState(modelConfig)


  const copyModel = () => Object.assign({}, model)


  function onParamChange(key, newVal) {
    const copy = copyModel()
    copy[key].v = parseFloat(newVal)
    setModel(copy)
    setHash()
  }


  function calculateDrakeNumber() {
    const m = copyModel()
    m.num.v = Math.round(
      m.starFormRate.v * m.fracWithPlanet.v * m.rateSupportLife.v *
        m.fracLife.v * m.fracIntel.v * m.fracCiv.v *
        m.civDuration.v)
    setModel(m)
    setHash()
  }


  function setHash() {
    const params = new URLSearchParams()
    Object.entries(model).map(
      ([k,config]) => {
        if (config.symbol) {
          return
        }
        params.set(k, config.v)
      }
    )
    location.hash = params.toString()
  }


  useEffect(() => {
    if (location.hash && location.hash !== '#') {
      let paramStr = location.hash
      if (paramStr.startsWith('#')) {
        paramStr = paramStr.substring(1)
      }
      const params = new URLSearchParams(paramStr)
      const m = copyModel()
      for (let [key, value] of params) {
        const val = parseFloat(value)
        if (Number.isFinite(val)) {
          m[key].v = val
        }
      }
      console.log(m)
      setModel(m)
    }
  }, [setModel])



  return (
    <>
      <h1>Drake Equation Calculator</h1>

      <p>The Drake Equation is defined
        &nbsp;<a href="https://en.wikipedia.org/wiki/Drake_equation">Wikipedia/Drake_equation</a></p>
        <br/>

      <p>Use the calculator below to play with it. This will update the URL to include your parameter
        choices.  Then you can share your link to give a working reference and the receiver will see
      the same parameters as you.</p>

      <p>Default values are from Wikipedia unless otherwise noted</p>

      <MathJaxContext>
        <table border="0">
          <tbody>
            <tr>
              {
                Object.entries(model).map(
                  ([k,config]) =>
                  <td key={k} style={{textAlign: 'center'}}>{config.symbol ? config.a : config.l}</td>
                )
              }
            </tr>
            <tr>
              {
                Object.entries(model).map(
                  ([k,config]) =>
                  <td key={k}>
                    {config.symbol ? '' :
                     <input
                       value={config.v}
                       onChange={(e) => onParamChange(k, e.target.value)}
                       style={{
                         border: 'none',
                         borderBottom: 'solid 1px lightgrey',
                         textAlign: 'center',
                       }}/>}
                  </td>
                )
              }
            </tr>
          </tbody>
        </table>
        <ol style={{listStyle: 'none'}}>
          {
            Object.entries(model).map(
              ([k,config]) =>
              <li key={k}>
                {config.l}&nbsp;&nbsp;{config.s} {
                  config.c ?
                    <cite>Default: {config.v} [<a href={config.c} target="new">ref</a>]</cite>
                  : config.c}
              </li>
            )
          }
        </ol>
      </MathJaxContext>
      <button onClick={calculateDrakeNumber}>Calculate</button>
    </>
  )
}
