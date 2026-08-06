const CACHE_NAME = 'hello-pwa-v1'
const URLS_TO_CACHE = [
  '/', // important so reload of root works
  '/index.html',
]


self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(URLS_TO_CACHE))
  )
})


self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k))
      )
    )
  )
})


self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') {
    return
  }

  event.respondWith(
    caches.match(event.request).then((cached) => {
      // If we have it cached, use it
      if (cached) {
        return cached
      }
      // Otherwise fall back to network
      return fetch(event.request).catch(() => {
        // For navigations, show the cached shell (or an offline page)
        if (event.request.mode === 'navigate') {
          return caches.match('/')  // or '/index.html'
        }

        // For other requests, you can return a generic fallback
        return new Response('Offline', {
          status: 503,
          statusText: 'Service Unavailable'
        })
      })
    })
  )
})
