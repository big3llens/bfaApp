package ru.bfad.bfaApp.webComponent.utilities;

import ru.bfad.bfaApp.webComponent.dto.HandbookPerson;

import java.util.List;

public class HandbookUtilities {

    StringBuilder sb = new StringBuilder();

    public void setOrder(List<HandbookPerson> list){
        for (HandbookPerson p: list) {
            sb.append(p.getTitle().split(" ")[0]);
            switch (sb.toString()){
                case "Генеральный": p.setOrder(1); break;
                case "Исполнительный": p.setOrder(2); break;
                case "Начальник":
                case "Руководитель": p.setOrder(3); break;
                case "Главный": p.setOrder(4); break;
                case "Заместитель":
                case "Зам.": p.setOrder(5); break;
                case "Старший": p.setOrder(6); break;
                case "Ведущий": p.setOrder(7); break;
                case "Специалист": p.setOrder(8); break;
                case "Менеджер": p.setOrder(9); break;
            }
            sb.setLength(0);
        }
    }
}
