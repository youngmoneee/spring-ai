/*
 * Copyright 2023 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ai.template;

import java.util.Map;
import java.util.Stack;

public class TemplateTreeFactory {

	public static TemplateTree buildTreeFromString(String s, Map<String, Object> source) {
		TemplateTree root = new TemplateTree("");
		Stack<TemplateTree> stack = new Stack<>();
		StringBuilder tempStr = new StringBuilder();

		stack.push(root);

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (i + 1 < s.length()) {
				char nextCh = s.charAt(i + 1);
				if (ch == '{' && nextCh == '{') {
					stack.peek().addChild(new TemplateTree(tempStr.toString()));
					tempStr = new StringBuilder().append('{');
					i++;
				}
				else if (ch == '}' && nextCh == '}') {
					tempStr.append('}');
					stack.peek().addChild(new TemplateTree(tempStr.toString()));
					tempStr = new StringBuilder();
					i++;
				}
				else if (ch == '{') {
					stack.peek().addChild(new TemplateTree(tempStr.toString()));
					tempStr = new StringBuilder();
					stack.push(new TemplateTree(null));
				}
				else if (ch == '}') {
					String var = tempStr.toString();
					TemplateTree node = stack.pop();
					node.setTemplate(source.getOrDefault(var.trim(), var).toString());
					tempStr = new StringBuilder();
					stack.peek().addChild(node);
				}
				else {
					tempStr.append(ch);
				}
			}
			else {
				tempStr.append(ch);
			}
		}

		if (tempStr.length() > 0) {
			stack.peek().addChild(new TemplateTree(tempStr.toString()));
		}

		return root;
	}

}
