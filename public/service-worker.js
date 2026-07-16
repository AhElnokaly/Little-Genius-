// Self-destructing Service Worker to immediately purge old PWA cache and unregister
self.addEventListener('install', function (e) {
  self.skipWaiting();
});

self.addEventListener('activate', function (e) {
  self.clients.claim().then(function () {
    self.registration.unregister().then(function () {
      console.log('PWA Service Worker unregistered successfully.');
    });
  });
});
