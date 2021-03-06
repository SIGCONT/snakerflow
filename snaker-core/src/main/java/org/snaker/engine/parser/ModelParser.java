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
package org.snaker.engine.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.snaker.engine.SnakerException;
import org.snaker.engine.core.ServiceContext;
import org.snaker.engine.helper.XmlHelper;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.TransitionModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 流程定义xml文件的模型解析器
 * @author yuqs
 * @since 1.0
 */
public class ModelParser {
	/**
	 * 解析流程定义文件，并将解析后的对象放入模型容器中
	 * @param bytes
	 */

	//解析流程定义xml文件，返回顶层Model对象
	public static ProcessModel parse(byte[] bytes) {
		DocumentBuilder documentBuilder = XmlHelper.createDocumentBuilder();
		if(documentBuilder != null) {
			Document doc = null;
			try {
				doc = documentBuilder.parse(new ByteArrayInputStream(bytes));

				//获取顶层process元素及属性填充到 ProcessModel 中
				Element processE = doc.getDocumentElement();
				ProcessModel process = new ProcessModel();
				process.setName(processE.getAttribute(NodeParser.ATTR_NAME));
				process.setDisplayName(processE.getAttribute(NodeParser.ATTR_DISPLAYNAME));
				process.setExpireTime(processE.getAttribute(NodeParser.ATTR_EXPIRETIME));
				process.setInstanceUrl(processE.getAttribute(NodeParser.ATTR_INSTANCEURL));
				process.setInstanceNoClass(processE.getAttribute(NodeParser.ATTR_INSTANCENOCLASS));

				NodeList nodeList = processE.getChildNodes();
				int nodeSize = nodeList.getLength();
				//遍历process元素下的子元素，解析得到NodeModel,填充到process的List<NodeModel> nodes字段中
				//除了process元素外，所有的子元素都是NodeModel
				for(int i = 0; i < nodeSize; i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						NodeModel model = parseModel(node);
						process.getNodes().add(model);
					}
				}
				
				//循环节点列表，构造变迁输入、输出的source、target
				//transition中添加输出目标NodeModel
				//NodeModel中添加输入来源transition集合
				for(NodeModel node : process.getNodes()) {
					for(TransitionModel transition : node.getOutputs()) {
						String to = transition.getTo();
						for(NodeModel node2 : process.getNodes()) {
							if(to.equalsIgnoreCase(node2.getName())) {
								node2.getInputs().add(transition);
								transition.setTarget(node2);
							}
						}
					}
				}

				//返回填充好的顶层ProcessModel
				return process;
			} catch (SAXException e) {
				e.printStackTrace();
				throw new SnakerException(e);
			} catch (IOException e) {
				throw new SnakerException(e);
			}
		} else {
			throw new SnakerException("documentBuilder is null");
		}
	}
	
	/**
	 * 对流程定义xml的节点，根据其节点对应的解析器解析节点内容
	 * @param node
	 * @return
	 */
	private static NodeModel parseModel(Node node) {
		String nodeName = node.getNodeName();
		Element element = (Element)node;
		NodeParser nodeParser = null;
		try {
			//根据元素名称从IOC容器中查找对应的parser来解析
			//parser的定义存储在base.config.xml中，在engine类的初始化阶段解析到IOC容器中
			//nodeParser有自身状态，无法并发使用
			nodeParser = ServiceContext.getContext().findByName(nodeName, NodeParser.class);
			nodeParser.parse(element);
			return nodeParser.getModel();
		} catch (RuntimeException e) {
			throw new SnakerException(e);
		}
	}
}
