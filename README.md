## Shiro实现的单点登录Demo
### 1.Host配置
````
  127.0.0.1    sso.scj.com
  127.0.0.1    app1.scj.com
  127.0.0.1    app2.scj.com
````
### 2.nginx配置
````
server {
        listen 80;
        server_name sso.scj.com;
        location / {
                if ($request_method = 'OPTIONS') {
                        add_header 'Access-Control-Allow-Origin' '*';
                        add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
                        add_header 'Access-Control-Allow-Headers' 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range';
                        add_header 'Access-Control-Max-Age' 1728000;
                        add_header 'Content-Type' 'text/plain charset=UTF-8';
                        add_header 'Content-Length' 0;
                        return 204;
                }
                proxy_set_header Host $host;
                proxy_set_header X-Real-Ip $remote_addr;
                proxy_set_header X-Forwarded-For $remote_addr;
                proxy_pass http://localhost:8080;
                proxy_set_header   Cookie $http_cookie;
                add_header 'Access-Control-Allow-Origin' "$http_origin";
                add_header 'Access-Control-Allow-Credentials' "true";#

        }
}
````

````
server {
        listen 80;
        server_name app1.scj.com;
        location / {
                proxy_set_header Host $host;
                proxy_set_header X-Real-Ip $remote_addr;
                proxy_set_header X-Forwarded-For $remote_addr;
                proxy_pass http://localhost:8081;
                proxy_set_header   Cookie $http_cookie;

        }
}
````

````
server {
        listen 80;
        server_name app2.scj.com;
        location / {
                proxy_set_header Host $host;
                proxy_set_header X-Real-Ip $remote_addr;
                proxy_set_header X-Forwarded-For $remote_addr;
                proxy_pass http://localhost:8082;
                proxy_set_header   Cookie $http_cookie;

        }
}
````

### 3.项目介绍
ssopublic 单点登陆公共包
ssoweb 单点登陆页面 第三方绑定模拟页面(这边通过识别请求为移动端跳转到绑定页面，这里是为了模拟，如果是微信平台，应该可以也通过agent来判断)
app1 模拟jsp页面(后端渲染)
app2 模拟前后端分离