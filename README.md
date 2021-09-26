# asm-demo
a demo for asm


1）塔建nexus私服
mac平台：
下载：https://www.sonatype.com/products/repository-oss-download

运行：xxx/nexus-3.30.1-01-mac/nexus-3.30.1-01/bin/nexus start

打开：localhost:8081，用admin登录，密码看提示。

2）gradle发布插件
新建maven仓库，参考：https://www.jianshu.com/p/52f4590abe33

要想通过maven发布，得在build.gradle中应用maven插件：

```groovy
apply plugin: 'com.android.library'
// 1）应用maven插件
apply plugin: 'maven'

// 2）配置GAV：group、artifact、version
group 'org.walker.widget'
version '1.0.0'

// 3）发布配置
uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: 'http://localhost:8081/nexus/content/repository/release') {
                authentication(userName: 'admin', password: 'admin123')
            }
            snapshotRepository(url: 'http://localhost:8081/nexus/content/repositories/snapshots') {
                authentication(userName: 'admin', password: 'admin123')
            }
            pom.artifactId = 'pullview'
            pom.packaging = 'aar'
        }
    }
}
```

3) 使用我们的maven库
```groovy
// ...
allprojects {
    repositories {
        jcenter()
        maven {
            url 'http://localhost:8081/nexus/content/groups/release'
        }
    }
}

dependencies {
    compile 'org.walker.widget:pullview:1.0.0'
}
```

