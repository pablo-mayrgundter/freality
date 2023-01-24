import React, {Suspense, useEffect, useState} from 'react'
import {useLocation} from 'react-router-dom'
import Drake from './Drake'


function App() {
  const location = useLocation()
  const [path, setPath] = useState('')

  useEffect(() => {
    setPath(location.pathname)
  }, [location])

  return (
    <div>
      <Suspense fallback={<div>Loading...</div>}>
        <Drake/>
      </Suspense>
    </div>
  )
}

export default App
