import React, {useEffect} from 'react'
import {createRoot} from 'react-dom/client'
import {BrowserRouter, Routes, Route, useNavigate} from 'react-router-dom'
import App from './App'


function Routed() {
  const nav = useNavigate()

  useEffect(() => {
    const referrer = document.referrer
    if (referrer) {
      const path = new URL(document.referrer).pathname
      if (path.length > 1) {
        nav(path)
      }
    }
  }, [])

  return (
    <Routes>
      <Route path="/*" element={<App/>}/>
    </Routes>
  )
}

export default function main() {
  const root = createRoot(document.getElementById('root'))
  root.render(
    <BrowserRouter>
      <Routed/>
    </BrowserRouter>)
}

main()
