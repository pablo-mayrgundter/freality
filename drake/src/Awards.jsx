import React, {useEffect, useState} from 'react'
import {useParams} from 'react-router'
import oscarsStr from './oscars'


export default function Drake() {
  const oscars = JSON.parse(oscarsStr)
  const [model, setModel] = useState(oscars)

  const copyModel = () => Object.assign({}, model)
  return (
    <>
      <h1>Awards Game</h1>
      <table border="1">
        <tbody>
          <tr><th>Film</th><th>Year</th><th>Oscar Awards</th><th>Oscar Nominations</th></tr>
          {
            Object.entries(model).map(
              ([k,config]) =>
              <tr key={k}>
                <td>{config.film}</td>
                <td>{config.year}</td>
                <td>{config.awards}</td>
                <td>{config.nominations}</td>
              </tr>
            )
          }
        </tbody>
      </table>
    </>
  )
}
