Welcome to the JAndFix wiki!
* [部分数据内容来自Hotfix](https://yq.aliyun.com/articles/74598)
``
# JAndFix

### 简述
* JAndFix是一种基于Java实现的轻量级Android实时热修复方案，它并不需要重新启动就能生效。JAndFix是在AndFix的基础上改进实现，AndFix主要是通过jni实现对method（ArtMethod）结构题内容的替换。JAndFix是通过Unsafe对象直接操作Java虚拟机内存来实现替换。


### Git
* [JAndFix:https://github.com/alibaba/JAndFix](https://github.com/alibaba/JAndFix)
### 参考
* [Unsafe](http://mishadoff.com/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/)
* [AndFix](https://github.com/alibaba/AndFix)
* [Hotfix](https://yq.aliyun.com/articles/74598)

### License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html])

Copyright (c) 2017, alibaba-inc.com
