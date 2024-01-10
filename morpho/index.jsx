import React, {useState} from 'react'
import {createRoot} from 'react-dom/client'


const container = document.getElementById('app')
const root = createRoot(container)

function genRandArr(length = 10, max = 100) {
  return Array.from({length: length}, () => Math.floor(Math.random() * max))
}


function applySort(array, sort) {
  let anySwaps
  do {
    anySwaps = false
    array = array.map((cur, index, arr) => {
      const {newVal, swapped} = sort(cur, index, arr)
      anySwaps |= swapped
      return newVal
    })
  } while (anySwaps)
  return array
}


function upsort1(cur, index, arr) {
  let newVal = cur
  let swapped = false
  if (index < arr.length - 1 && cur > arr[index + 1]) {
    newVal = arr[index + 1]
    swapped = true
  } else if (index > 0 && cur < arr[index - 1]) {
    newVal = arr[index - 1]
    swapped = true
  }
  return {newVal, swapped}
}


function areArraysEqual(arr1, arr2) {
  return arr1.length === arr2.length
    && arr1.reduce((equal, curr, index) => equal && curr === arr2[index], true)
}



function Page() {
  const [arr] = useState(genRandArr(5, 100))
  const sorted = Array.from(arr).sort((a, b) => a - b)
  const result1 = applySort(arr, upsort1)
  return (
    <div>
      <div>Random array: {arr.join(', ')}</div>
      <div>Sorted: {sorted.join(', ')}</div>
      <div>upsort1: {result1.join(', ')}</div>
      <div>valid: {new Boolean(areArraysEqual(sorted, result1)).toString()}</div>
    </div>
  )
}
root.render(<Page/>)
