**Spring有两个自带接口、BeanFactory和FactoryBean、这两个接口虽然名字差不多、但是实际上确实完全不一样的两种东西**


----------


## 1、BeanFactory

BeanFactory定义了IOC容器的最基本形式、并提供了IOC容器应遵守的的最基本的接口、  也就是Spring IOC所遵守的最底层和最基本的编程规范、  在Spring代码中、BeanFactory只是个接口、并不是 IOC 容器的具体实现、  

但是Spring容器给出了很多种实现、如DefaultListableBeanFactory、XmlBeanFactory、ApplicationContext等、都是附加了某种功能的实现、具体我们可以在Eclipse中打开类结构、

![BeanFactory类结构图](http://img.blog.csdn.net/20160827180724689)

![BeanFactory类体系图](http://img.blog.csdn.net/20160827180834921)

```
package org.springframework.beans.factory;
import org.springframework.beans.BeansException;

public interface BeanFactory {
	String FACTORY_BEAN_PREFIX = "&";
	Object getBean(String name) throws BeansException;
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType) throws BeansException;
	Object getBean(String name, Object... args) throws BeansException;
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;
	boolean containsBean(String name);
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException;
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
	String[] getAliases(String name);
}
```


**在该接口中定义了一下6种方法**

 - boolean containsBean(String)：如果BeanFactory包含给定名称的bean定义(或bean实例)、则返回true、
 - Object getBean(String)：返回以给定名字注册的bean实例、根据bean的配置情况、如果为singleton模式将返回一个共享的实例、否则将返回一个新建的实例、如果没有找到指定的bean、该方法可能会抛出BeansException异常(实际上将抛出NoSuchBeanDefinitionException异常)、在对bean进行实例化和预处理时也可能抛出异常、
 - Object getBean(String, Class)：返回以给定名称注册的bean实例、并转换为给定class类型的实例、如果转换失败、相应的异常(BeanNotOfRequiredTypeException)将被抛出、上面的getBean(String)方法也适用该规则、
 - Class getType(String name)：返回给定名称的bean的Class、如果没有找到指定的bean实例、则抛出NoSuchBeanDefinitionException异常、
 - boolean isSingleton(String)：判断给定名称的bean定义(或bean实例)是否为singleton模式(singleton将在bean的作用域中讨论)、如果bean没找到、则抛出NoSuchBeanDefinitionException异常、
 - String[] getAliases(String)：返回给定bean名称的所有别名、

<font color="red">
BeanFactory它的职责包括：实例化、定位、配置应用程序中的对象及建立这些对象间的依赖、
BeanFactory是访问Spring beans的一个容器、所有的Spring Beans的定义都会在这里被统一的处理、
换句话说、BeanFactory Interface是一个应用组件（Spring Bean）的集中注册器和配置器、
从一般意义上来讲、BeanFactory是用来加载和管理Spring Bean Definition的、
</font>