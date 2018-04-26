# file-1

文件服务器的需求（Document、GridFS）

该文件CRUD服务致力于小型文件的存储，如博客中的图片、普通文档等。

Mongodb支持多种数据格式的存储，对于二进制的存储Mongodb数据库也不例外。因此，它能够很方便的用于存储文件。

鉴于Mongodb的BSON文档的数据量大小的限制（每个文档不超过16M），所有本文的文件服务器补充了Mongodb GridFS文件存储。

## mongodbGFS

上传图片：http://localhost:8788/file/uploadgfs
在线预览图片：http://localhost:8788/file/image/5ae17d5829bbf60798c6b618

