# GankBeauty
干货集中营（gank.io）福利图

技术栈：RecyclerView + Glide + OkHttp + Gson + Retrofit

开发环境： Windows 10 + Android Studio2.3.2

兼容 Android 4.0 ~ 7.0

主界面展示妹子缩略图列表， 点击图片可以查看大图，长按大图可以保存到本地。文件管理器打开存储，在Pictures/GankBeauty目录里面。

使用Glide保存图片，代码中有两种方式，其中使用bitmap的方式会造成图片比在电脑让下载的原图体积变大，而使用downloadOnly的方式实现的，保存的图片大小与从电脑上下载的图片对比，大小一致，md5也一致。因此bitmap方式的代码注释掉了，推荐使用downloadOnly的方式
