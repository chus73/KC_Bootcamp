package com.kc.console.config;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile(value = { "default", "dev" })
public class RestMappingInfo {

	@EventListener
	public void event(ApplicationReadyEvent event) {

		Map<String, Object> restControllers = event.getApplicationContext().getBeansWithAnnotation(Controller.class);
		StringBuilder root = new StringBuilder();

		for (Map.Entry<String, Object> entry : restControllers.entrySet()) {

			Class<?> controllerClass = entry.getValue().getClass();
			RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);

			if (classMapping != null) {
				root.append(classMapping.value()[0]);
			}

			for (Method m : controllerClass.getDeclaredMethods()) {
				printInfo(root.toString(), m);
			}

			root.setLength(0);
		}
	}

	private void printInfo(String root, Method m) {

		GetMapping get = m.getAnnotation(GetMapping.class);
		PostMapping post = m.getAnnotation(PostMapping.class);
		RequestMapping request = m.getAnnotation(RequestMapping.class);

		StringBuilder builder = new StringBuilder();

		if (get != null) {
			builder.append(" [GET] ");
			builder.append(root);
			builder.append(get.value()[0]);
		} else if (post != null) {
			builder.append(" [POST] ");
			builder.append(root);
			builder.append(post.value()[0]);
		} else if (request != null && request.method().length > 0 && request.value().length > 0) {
			builder.append(" [" + request.method()[0].name() + "] ");
			builder.append(root);
			builder.append(request.value()[0]);
		}

		if (!StringUtils.isEmpty(builder.toString())) {
			log.info("REST Mapping loaded: {}", builder.toString());
		}
	}

}
