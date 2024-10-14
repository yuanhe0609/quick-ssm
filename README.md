# ssm项目快速搭建
## 整合了mybatis,redis,mongodb,rabbitmq,swagger的快速开发框架
### 1.项目配置
#### 开发环境
* server：tomcat 9.0.93
* jdk：jdk 22
* maven：maven 3.9.8
* spring：spring 4.3.6.RELEASE
* mysql：mysql 8.0.28
* mybatis：mybatis 3.5.2
* redis：redis 5.0.14.1
* mongodb：mongodb 8.0
* swagger：swagger 2.0
### 2.自定义配置方式
只需在webapp/WEB-INF下的web.xml下进行配置
```
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                      http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0"
         metadata-complete="true">
    <!--启动后初始页面设置-->
    <welcome-file-list>
        <welcome-file>/jsp/index.jsp</welcome-file>
    </welcome-file-list>
    <!--配置前端控制器-->
    <servlet>
        <servlet-name>springMVC</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

        <!--配置springmvc的配置文件-->
        <!--在此配置需要的服务，不需要的服务只需删掉配置文件路径-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/spring-*.xml, 配置spring(springdao，springmvc，springservice)
                         classpath:swagger/spring-*.xml, 配置swagger (url地址:http://{ip}:{port}/{project_name}/swagger-ui.html)
                         classpath:redis/spring-*.xml, 配置redis
                         classpath:mongodb/spring-*.xml 配置mongodb
            </param-value>
        </init-param>
        <load-on-startup>
            1
        </load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>springMVC</servlet-name>
        <!--直接拦截所有请求，不再采用spring2.0的/*或者*.do方式-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
### 3.实现功能
#### 1. 获取一线生产人员出勤记录表
#### 2. 根据出勤记录表,计算出生产人员每日工作时间(细分为平日正常工作时间,平日加班工作时间,周末正常工作时间,周末加班时间,节日正常工作时间,节日加班时间)
#### 3. 根据生产人员每日工作时间,计算出生产人员每月总工作时间(细分为平日正常工作小时数,平日加班小时数,周末工作小时数,节日工作小时数)
#### 4. 根据每月总工作时间,计算出生产人员每月应发工资
###### 注:目前工作时间没有扣除中间休息时间


