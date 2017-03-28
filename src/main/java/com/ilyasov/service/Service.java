package com.ilyasov.service;

import com.ilyasov.dao.impl.DepartmentDAO;
import com.ilyasov.model.Department;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Service {
    final static Logger logger = Logger.getLogger(Service.class);
    DepartmentDAO departmentDAO = new DepartmentDAO();

    public void syncronizeWithXml() {
        HashMap<Integer, Department> xmlData = readXML();
        departmentDAO.update(xmlData);
    }

    public void buildXml() {
        try {
            logger.info("Building XML started.");
            HashMap<Integer, Department> databaseData = departmentDAO.read();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("work");
            document.appendChild(rootElement);

            Element department = document.createElement("department");
            rootElement.appendChild(department);
            department.setAttribute("depcode", "123");

            Element depJob = document.createElement("depjob");
            depJob.appendChild(document.createTextNode("IT"));
            department.appendChild(depJob);

            Element description = document.createElement("description");
            description.appendChild(document.createTextNode("Work in IT sphere"));
            department.appendChild(description);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            System.out.println("Input path of file in which you want to record data from database:");
            Scanner scanner = new Scanner(System.in);
            String path = scanner.nextLine();
            StreamResult result = new StreamResult(new File(path));

            transformer.transform(source, result);
            logger.info("Building XML completed successfully.");
        } catch (ParserConfigurationException e) {
            logger.error("Error in building XML.");
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            logger.error("Error in building XML.");
            e.printStackTrace();
        } catch (TransformerException e) {
            logger.error("Error in building XML.");
            e.printStackTrace();
        }
    }

    public HashMap<Integer, Department> readXML() {
        logger.info("Reading XML started.");
        try {
            HashMap<Integer, Department> departmentsFromXml = new HashMap<Integer, Department>();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Input path of file:");
            String path = scanner.nextLine();
            File inputFile = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("department");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Department department = new Department();
                    department.setDepCode(element.getAttribute("depcode"));
                    department.setDepJob(element.getElementsByTagName("depjob").item(0).getTextContent());
                    department.setDescription(element.getElementsByTagName("description").item(0).getTextContent());
                    departmentsFromXml.put(i, department);
                }
            }

            //checking PrimaryKey of departments
            for (int i = 0; i < departmentsFromXml.size(); i++) {
                for (int j = 0; j < departmentsFromXml.size(); j++) {
                    if (departmentsFromXml.get(i).getDepJob().equals(departmentsFromXml.get(j).getDepJob()) &&
                            departmentsFromXml.get(i).getDepCode().equals(departmentsFromXml.get(j).getDepCode()) && i != j) {
                        System.out.println("Error in XML data: similar PrimaryKey values in 2 departments");
                        logger.error("Error in XML data: similar PrimaryKey values in 2 departments.");
                        System.exit(1);
                    }
                }
            }
            logger.info("Reading XML completed successfully.");
            return departmentsFromXml;
        } catch (ParserConfigurationException e) {
            logger.error("Error in reading XML.");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Error in reading XML.");
            e.printStackTrace();
        } catch (SAXException e) {
            logger.error("Error in reading XML.");
            e.printStackTrace();
        }
        return null;
    }


}