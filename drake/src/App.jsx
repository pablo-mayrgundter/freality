import React, {useState} from 'react'
import Awards from './Awards'
import Drake from './Drake'


export default function App() {
  const [isAwardsShowing, setIsAwardsShowing] = useState(false)
  const [isDrakeShowing, setIsDrakeShowing] = useState(false)
  function handleClick(showAwards = false, showDrake = false) {
    setIsAwardsShowing(showAwards)
    setIsDrakeShowing(showDrake)
  }
  return (
    <>
      <button onClick={() => handleClick(true, false)}>Awards</button>
      <button onClick={() => handleClick(false, true)}>Drake</button>
      <hr/>
      {isAwardsShowing && <Awards/>}
      {isDrakeShowing && <Drake/>}
    </>
  )
}
