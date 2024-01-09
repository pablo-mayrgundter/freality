import React from 'react'
import {createRoot} from 'react-dom/client'


const container = document.getElementById('app')
const root = createRoot(container)

const arr = Array.from({length: 10}, () => Math.floor(Math.random() * 100))

const Page = () => (
  <div>
    <div>Random array: {arr.join(', ')}</div>
    <div>Sorted: {arr.sort((a, b) => a - b).join(', ')}</div>
  </div>
)
root.render(<Page/>)
