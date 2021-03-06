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
package org.snaker.engine.model;

import java.io.Serializable;

import org.snaker.engine.core.Execution;
import org.snaker.engine.handlers.IHandler;

/**
 * 模型元素基类
 * @author yuqs
 * @since 1.0
 */

//BaseModel只有name和displayName两个属性
//displayName一般是用户可识别的中文名称，name用作标识节点的id
public class BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3082741431225739241L;
	/**
	 * 元素名称
	 */
	private String name;
	/**
	 * 显示名称
	 */
	private String displayName;
	
	/**
	 * 将执行对象execution交给具体的处理器处理
	 * @param handler
	 * @param execution
	 */

	//仅起到中转调用的效果，不会改变自身状态
	protected void fire(IHandler handler, Execution execution) {
		handler.handle(execution);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
