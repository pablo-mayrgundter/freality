/**
 * Usage:
 *
 * Paste this into console on your x.com/notifications page
 *
 * Then call reportSpamAndBlockFirst() once to remove the top follow notification
 */

function getElementByTextContent(selector, text, index = 0) {
  const elements = document.querySelectorAll(selector)
  const matches = []
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].textContent.trim().includes(text)) {
      if (index === 0) {
        return elements[i]
      }
      index--
    }
  }
}

function getFirstElementByTextContent(selector, text) {
  return getElementByTextContent(selector, text)
}

function getFollow() {
  const follow = getFirstElementByTextContent('span', 'followed you')
  return follow.parentNode
}

const log = (msg) => console.log(msg)

const pauseTime = 1000
function reportSpamAndBlockFirst() {
  log('reportSpamAndBlockFirst')
  getFollow().click()
  setTimeout(reportSpamAndBlock, pauseTime)
}

function reportSpamAndBlock() {
  log('clickMore')
  document.querySelector('[aria-label="More"]').click()
  setTimeout(clickReport, pauseTime)
}

function clickReport() {
  log('clickReport')
  getFirstElementByTextContent('span', 'Report').click()
  setTimeout(clickSpamNext, pauseTime)
}

function clickSpamNext() {
  log('clickSpamNext')
  getFirstElementByTextContent('span', 'Spam').click()
  getFirstElementByTextContent('span', 'Next').click()
  setTimeout(blockDoneBack, pauseTime)
}

function blockDoneBack() {
  log('blockDoneBack')
  getElementByTextContent('span', 'Block', 2).click()
  getFirstElementByTextContent('span', 'Done').click()
  setTimeout(clickBack, pauseTime)
}

function clickBack() {
  document.querySelector('[aria-label="Back"]').click()
}
