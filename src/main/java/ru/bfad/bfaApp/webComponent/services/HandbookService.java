package ru.bfad.bfaApp.webComponent.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.bfad.bfaApp.webComponent.dto.HandbookPerson;
import ru.bfad.bfaApp.webComponent.repositories.LdapPersonRepository;
import ru.bfad.bfaApp.webComponent.utilities.HandbookUtilities;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HandbookService {
    @Autowired
    private LdapPersonRepository ldapPersonRepository;

    private final HandbookUtilities utilities = new HandbookUtilities();

    public void getHandbookPersons(Model model, String company){
        if (company.equals("ou=bfa development,ou=bfa users,dc=bfad,dc=ru")) getDevelopmentPersons(model, company);
        try {
            model.addAttribute("HandbookPerson", ldapPersonRepository.getAllPersonForHandbook(company));
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
    }

    public void getDevelopmentPersons(Model model, String company){
        StringBuilder sb = new StringBuilder();
        List<HandbookPerson> list = null;
        try {
            list = ldapPersonRepository.getAllPersonForHandbook(company);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

        List<HandbookPerson> managment = new ArrayList<>();
        List<HandbookPerson> protocolSector = new ArrayList<>();
        List<HandbookPerson> personnelManagement = new ArrayList<>();
        List<HandbookPerson> isu = new ArrayList<>();
        List<HandbookPerson> isuDepartments = new ArrayList<>();
        List<HandbookPerson> CCAndQD = new ArrayList<>();
        List<HandbookPerson> CCAndQDDepartment = new ArrayList<>();
        List<HandbookPerson> marketingAndSales = new ArrayList<>();
        List<HandbookPerson> marketingAndSalesDepartment = new ArrayList<>();
        List<HandbookPerson> legalManagement = new ArrayList<>();
        List<HandbookPerson> financialManagement = new ArrayList<>();
        List<HandbookPerson> financialDepartment = new ArrayList<>();
        List<HandbookPerson> accounting = new ArrayList<>();
        List<HandbookPerson> technicalService = new ArrayList<>();
        List<HandbookPerson> operationsDepartment = new ArrayList<>();
        List<HandbookPerson> motorTransportDepartment = new ArrayList<>();

        for (HandbookPerson person: list) {
            switch (person.getDepartment()){
                case "Руководство": managment.add(person); break;
                case "Протокольный сектор": protocolSector.add(person); break;
                case "Управление по кадрам и защите информации":
                case "Отдел по защите информации":
                case "Отдел кадров": personnelManagement.add(person); break;
                case "Инженерно-строительное управление": isu.add(person); break;
                case "Отдел согласования":
                case "Отдел претензионной работы":
                case "Планово-экономический отдел":
                case "Отдел подготовки проектной документации":
                case "Служба сопровождения проектов":
                case "Отдел инженерного обеспечения":
                case "Договорный отдел": isuDepartments.add(person);break;
                case "Управление строительного контроля и качества":CCAndQD.add(person);break;
                case "Отдел строительного контроля и качества":
                case "Служба геодезического контроля и мониторинга":CCAndQDDepartment.add(person);break;
                case "Управление маркетинга и продаж":marketingAndSales.add(person); break;
                case "Отдел продаж":
                case "Отдел маркетинга и рекламы":marketingAndSalesDepartment.add(person); break;
                case "Юридическое управление":
                case "Отдел недвижимости":legalManagement.add(person); break;
                case "Финансовое управление": financialManagement.add(person); break;
                case "Отдел управленческого учета":
                case "Отдел бизнес-планирования": financialDepartment.add(person); break;
                case "Бухгалтерия": accounting.add(person); break;
                case "Отдел технической поддержки":technicalService.add(person); break;
                case "Отдел эксплуатации": operationsDepartment.add(person); break;
                case "Автотранспортный отдел":motorTransportDepartment.add(person); break;
            }
        }

        utilities.setOrder(managment);
        utilities.setOrder(protocolSector);
        utilities.setOrder(personnelManagement);
        utilities.setOrder(isu);
        utilities.setOrder(isuDepartments);
        utilities.setOrder(CCAndQD);
        utilities.setOrder(CCAndQDDepartment);
        utilities.setOrder(marketingAndSales);
        utilities.setOrder(marketingAndSalesDepartment);
        utilities.setOrder(legalManagement);
        utilities.setOrder(financialManagement);
        utilities.setOrder(financialDepartment);
        utilities.setOrder(accounting);
        utilities.setOrder(technicalService);
        utilities.setOrder(operationsDepartment);
        utilities.setOrder(motorTransportDepartment);

        Collections.sort(list);
        Collections.sort(managment);
        Collections.sort(protocolSector);
        Collections.sort(personnelManagement);
        Collections.sort(isu);
        Collections.sort(isuDepartments);
        Collections.sort(CCAndQD);
        Collections.sort(CCAndQDDepartment);
        Collections.sort(marketingAndSales);
        Collections.sort(marketingAndSalesDepartment);
        Collections.sort(legalManagement);
        Collections.sort(financialManagement);
        Collections.sort(financialDepartment);
        Collections.sort(accounting);
        Collections.sort(technicalService);
        Collections.sort(operationsDepartment);
        Collections.sort(motorTransportDepartment);

        model.addAttribute("managment", managment);
        model.addAttribute("protocolSector", protocolSector);
        model.addAttribute("personnelManagement", personnelManagement);
        model.addAttribute("isu", isu);
        model.addAttribute("isuDepartments", isuDepartments);
        model.addAttribute("CCAndQD", CCAndQD);
        model.addAttribute("CCAndQDDepartment", CCAndQDDepartment);
        model.addAttribute("marketingAndSales", marketingAndSales);
        model.addAttribute("marketingAndSalesDepartment", marketingAndSalesDepartment);
        model.addAttribute("legalManagement", legalManagement);
        model.addAttribute("financialManagement", financialManagement);
        model.addAttribute("financialDepartment", financialDepartment);
        model.addAttribute("accounting", accounting);
        model.addAttribute("technicalService", technicalService);
        model.addAttribute("operationsDepartment", operationsDepartment);
        model.addAttribute("motorTransportDepartment", motorTransportDepartment);
    }

}
