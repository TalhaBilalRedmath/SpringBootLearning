const { createProxyMiddleware } = require('http-proxy-middleware');
 
module.exports = function (app) {
  // Only proxy backend endpoints:
  app.use(
    [
      '/api',
     
      '/oauth2/authorization'  
    ],
    createProxyMiddleware({
      target: 'http://localhost:8080',
      changeOrigin: true,
      cookieDomainRewrite: 'localhost',
      xfwd: true,
      logLevel: 'silent',
    })
  );
};