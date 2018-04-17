package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.service.ApiUsageService;
import com.CLD.dataAnonymization.service.ApiUsageServiceImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
