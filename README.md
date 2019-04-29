## Shiro实现的单点登录Demo
### 技术栈
1. springboot
2. shiro
3. redis
4. nginx
### 配置
#### Host配置
````
  127.0.0.1    sso.scj.com
  127.0.0.1    app1.scj.com
  127.0.0.1    app2.scj.com
````
#### nginx配置
使用nginx是为了让上面host的访问都是通过80端口，也就是不需要输入端口来访问
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
#### redis配置
安装一个redis即可,在application.properties做对应设置

#### 应用介绍
ssopublic 单点登陆公共包  
ssoweb 单点登陆页面 包括手机端和web端(通过http报文中的agent区别)  
app1 模拟jsp页面(后端渲染)  
app2 模拟前后端分离模式  

#### 相关文章
[基于Shiro实现的单点登陆系统](https://www.jianshu.com/p/611b19dbfb04)  
[使用Shiro开发免登录功能](https://www.jianshu.com/p/4c32e29bfb3b)

#### 我的公众号
![公众号](http://upload-images.jianshu.io/upload_images/9919411-cdb3cba0f4d6d039..jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)