package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.service.ApiUsageService;
import com.CLD.dataAnonymization.service.ApiUsageServiceImpl;
import com.CLD.dataAnonymization.util.deidentifier.FileResolve;
import javafx.application.Application;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


@SpringBootApplication
public class DataAnonymizationApplication {


	public static void main(String[] args) {
		SpringApplication.run(DataAnonymizationApplication.class, args);
	}
}
