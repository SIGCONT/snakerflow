/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snaker.engine.spring;

import org.snaker.engine.core.SnakerEngineImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Properties;

/**
 * spring环境使用的SnakerEngine实现类，主要接收spring的applicationContext对象
 * @author yuqs
 * @since 1.0
 */

//使用继承实现的engine类， 提供给应用层使用，需要接收IOC容器ApplicationContext
public class SpringSnakerEngine extends SnakerEngineImpl
        implements InitializingBean, ApplicationContextAware {

	private ApplicationContext applicationContext;
    private Properties properties;


    //容器放开的bean初始化方法，在bean被实例化且属性都注入后被调用
    //主要执行初始化工作，比如解析snaker开放给应用的配置文件
	public void afterPropertiesSet() throws Exception {

        //在构造函数中把IOC容器传到snaker的服务注册中心ServiceContext
        //调用顺序ServiceContext->SpringContext->ApplicationContext最终还是调用到传进来的IOC容器
        SpringConfiguration configuration = new SpringConfiguration(applicationContext);
        if(properties != null) 
            configuration.initProperties(properties);

        //传入IOC容器构造configuration对象，并且开始snaker的配置解析和初始化工作,configuration对象用完即弃
        configuration.parser();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
