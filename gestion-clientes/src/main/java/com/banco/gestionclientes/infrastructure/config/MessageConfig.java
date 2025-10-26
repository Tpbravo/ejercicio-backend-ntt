package com.banco.gestionclientes.infrastructure.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class MessageConfig extends AcceptHeaderLocaleResolver {

	@Bean
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:ValidationMessages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(false);
		return messageSource;
	}

	@Bean
	LocaleResolver localeResolver() {
		return this; // Usa el Accept-Language del request
	}

	@SuppressWarnings("deprecation")
	@Override
	public Locale resolveLocale(jakarta.servlet.http.HttpServletRequest request) {
		String headerLang = request.getHeader("Accept-Language");
		return (headerLang == null || headerLang.isEmpty()) ? new Locale("es") : Locale.forLanguageTag(headerLang);
	}

	@Bean
	LocalValidatorFactoryBean defaultValidator(MessageSource messageSource) {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource);
		return bean;
	}
}
