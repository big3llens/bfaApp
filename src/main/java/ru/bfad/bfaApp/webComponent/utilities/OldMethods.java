package ru.bfad.bfaApp.webComponent.utilities;

public class OldMethods {
//    public void sendMessage (MessageFrom message, String principal){
//        String url = "http://ismaks.bfa-d.ru:31880/api/send_mail";
//
////        System.out.println("Principal: " + principal.getName());
////        MessageTo messageTo = saveMessage(message, principal);
//        MessageTo messageTo = createMessageTo(message);
//
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
//        MappingJackson2HttpMessageConverter converter2 = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
//        messageConverters.add(converter);
//        messageConverters.add(converter2);
//        restTemplate.setMessageConverters(messageConverters);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json; charset=utf-8");
//
//        HttpEntity<MessageTo> entity = new HttpEntity<>(messageTo, headers);
//
//        System.out.println("зашел");
//        System.out.println(message);
//        System.out.println(messageTo);
//        System.out.println(restTemplate.postForObject(url, entity, SagalovResponse.class));
//    }

    //    public String sendMessage(MessageFrom message){
//        MessageTo messageTo = createMessageTo(message);
//        System.out.println(messageTo.toString());
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://ismaks.bfa-d.ru:31880/api/send_mail");
////        HttpPost httpPost = new HttpPost("http://10.160.3.232:7981/handbook/ldap/postTest");
////        StringBody from = new StringBody( messageTo.getFrom(), ContentType.MULTIPART_FORM_D
////        HttpPost httpPost = new HttpPost("http://ismaks.bfa-d.ru:31880/api/send_mail");ATA);
////        StringBody subject = new StringBody( messageTo.getSubject(), ContentType.MULTIPART_FORM_DATA);
////        StringBody text = new StringBody( messageTo.getText(), ContentType.MULTIPART_FORM_DATA);
////        StringBody to = new StringBody( messageTo.getTo().toString(), ContentType.MULTIPART_FORM_DATA);
////        StringBody deliveryMethod = new StringBody( String.valueOf(messageTo.getDeliveryMethod()), ContentType.MULTIPART_FORM_DATA);
////        StringBody sendBodyMethod = new StringBody( String.valueOf(messageTo.getSendBodyMethod()), ContentType.MULTIPART_FORM_DATA);
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        ContentType contentType = ContentType.create("application/json", Consts.UTF_8);
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        builder.setCharset(Consts.UTF_8);
//        builder.addTextBody("from", messageTo.getFrom(), ContentType.DEFAULT_BINARY);
//        builder.addTextBody("subject", messageTo.getSubject(), contentType);
//        builder.addTextBody("text", messageTo.getText(), contentType);
//        builder.addTextBody("to", messageTo.getAttachmentString(), ContentType.DEFAULT_BINARY);
//        builder.addTextBody("deliveryMethod", String.valueOf(messageTo.getDeliveryMethod()), ContentType.DEFAULT_BINARY);
//        builder.addTextBody("sendBodyMethod", String.valueOf(messageTo.getSendBodyMethod()), ContentType.DEFAULT_BINARY);
////        System.out.println("проверяем влежение в messageTo: " + messageTo.getFile().getOriginalFilename());
//        if(messageTo.getFile() != null){
//            System.out.println("вкладываем файл тела");
//            File file = saveFileFromMessage(messageTo.getFile(), (byte) 0);
//            System.out.println("файл тела: " + file.getName());
//            builder.addBinaryBody("fileBody" ,file, ContentType.DEFAULT_BINARY, file.getName());
//        }
//        if(messageTo.getAttachment() != null){
//            for (MultipartFile at: messageTo.getAttachment()) {
//                System.out.println("вкладываем вложение");
//                File file = saveFileFromMessage(at, (byte) 1);
//                builder.addBinaryBody("attachment" ,file, ContentType.DEFAULT_BINARY, file.getName());
//            }
//        }
//        org.apache.http.HttpEntity entity = builder.build();
//        httpPost.setEntity(entity);
//        try {
//            HttpResponse response = client.execute(httpPost);
//            org.apache.http.HttpEntity responseEntity = response.getEntity();
//            if (responseEntity != null) {
//                // Конвертировать содержимое ответа на строку
//                return EntityUtils.toString(responseEntity, Charsets.UTF_8);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "что-то пошло не так";
//    }
//
//    public String sendFileTest(MessageFrom message){
//        MessageTo messageTo = createMessageTo(message);
//        System.out.println(messageTo.toString());
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://10.160.3.232:7981/handbook/ldap/file");
//
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        File file = saveFileFromMessage(messageTo.getFile(), (byte) 0);
//        FileBody fileBody= new FileBody(file, ContentType.DEFAULT_BINARY);
//        System.out.println("проверяем влежение в messageTo: " + messageTo.getFile().getOriginalFilename());
//        builder.addPart("file", fileBody);
////        if(messageTo.getFile() != null){
////            System.out.println("вкладываем файл тела");
////            File file = saveFileFromMessage(messageTo.getFile());
////            System.out.println("файл тела: " + file.getName());
////            builder.addBinaryBody("fileBody" ,file, ContentType.DEFAULT_BINARY, file.getName());
////        }
//
//        org.apache.http.HttpEntity entity = builder.build();
//        httpPost.setEntity(entity);
//        try {
//            HttpResponse response = client.execute(httpPost);
////            org.apache.http.HttpEntity responseEntity = response.getEntity();
////            if (responseEntity != null) {
////                // Конвертировать содержимое ответа на строку
////                return EntityUtils.toString(responseEntity, Charsets.UTF_8);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "что-то пошло не так";
//    }

    //    public void sendMessageOld (MessageFrom message){
//        String url = "http://ismaks.bfa-d.ru:31880/api/send_mail";
//
////        System.out.println("Principal: " + principal.getName());
////        MessageTo messageTo = saveMessage(message, principal);
//        MessageTo messageTo = createMessageTo(message);
//
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
//        MappingJackson2HttpMessageConverter converter2 = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
//        MappingJackson2HttpMessageConverter converter3 = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));
//        messageConverters.add(converter);
//        messageConverters.add(converter2);
//        messageConverters.add(converter3);
//        restTemplate.setMessageConverters(messageConverters);
//
//        HttpHeaders headers = new HttpHeaders();
////        headers.set("Content-Type", "application/json; charset=utf-8");
////        headers.set("Content-Type", "multipart/form-data; charset=utf-8");
////        headers.set("Content-Type", "application/json");
//        headers.set("Content-Type", "multipart/form-data");
//        File bodyFile = saveFileFromMessage(messageTo.getFile(), (byte) 0);
//        FileSystemResource fsr = new FileSystemResource(bodyFile);
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("from", messageTo.getFrom());
//        body.add("subject", messageTo.getSubject());
//        body.add("text", messageTo.getText());
//        body.add("to", messageTo.getNiceTo());
//        body.add("deliveryMethod", messageTo.getDeliveryMethod());
//        try {
//            body.add("image[]", messageTo.getFile().getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        try {
////            body.add("image[]", bodyFile);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
////        HttpEntity<MessageTo> entity = new HttpEntity<>(messageTo, headers);
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        System.out.println("зашел");
//        System.out.println(message);
//        System.out.println(messageTo);
//        System.out.println(restTemplate.postForObject(url, entity, SagalovResponse.class));
//    }
//    public void sendMessageOld (MessageFrom message){
//        String url = "http://ismaks.bfa-d.ru:31880/api/send_mail";
//
////        System.out.println("Principal: " + principal.getName());
////        MessageTo messageTo = saveMessage(message, principal);
//        MessageTo messageTo = createMessageTo(message);
//
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
//        MappingJackson2HttpMessageConverter converter2 = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
//        MappingJackson2HttpMessageConverter converter3 = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));
//        messageConverters.add(converter);
//        messageConverters.add(converter2);
//        messageConverters.add(converter3);
//        restTemplate.setMessageConverters(messageConverters);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json; charset=utf-8");
////        headers.set("Content-Type", "multipart/form-data; charset=utf-8");
////        headers.set("Content-Type", "application/json");
////        headers.set("Content-Type", "multipart/form-data");
//
//        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
//        ContentDisposition contentDisposition = ContentDisposition
//                .builder("form-data")
//                .name("file")
//                .filename(messageTo.getFile().getOriginalFilename())
//                .build();
//        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
//        try {
//            HttpEntity<byte[]> fileEntity = new HttpEntity<>(messageTo.getFile().getBytes(), fileMap);
//            File bodyFile = saveFileFromMessage(messageTo.getFile(), (byte) 0);
//            FileSystemResource fsr = new FileSystemResource(bodyFile);
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("from", messageTo.getFrom());
//            body.add("subject", messageTo.getSubject());
//            body.add("text", messageTo.getText());
//            body.add("to", messageTo.getNiceTo());
//            body.add("deliveryMethod", messageTo.getDeliveryMethod());
//            body.add("image[]", fileEntity);
//            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
//            System.out.println("зашел");
//            System.out.println(message);
//            System.out.println(messageTo);
//            System.out.println(restTemplate.postForObject(url, entity, String.class));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        try {
////            body.add("image[]", bodyFile);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
////        HttpEntity<MessageTo> entity = new HttpEntity<>(messageTo, headers);
//    }


//    public String sendFileToSagalov(MultipartFile f){
//        File senderFile = saveFileFromMessage(f, (byte) 0);
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("name", "image"));
//        try (CloseableHttpClient httpClient = HttpClients.createDefault();){
//            // Создать запрос на запись
////            HttpPost httpPost = new HttpPost("http://ismaks.bfa-d.ru:31880/api/send_bodyasattach");
//            HttpPost httpPost = new HttpPost("http://10.160.3.232:7981/handbook/ldap/file");
//            // Конструировать многокомпонентный порубительский строитель
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setCharset(Charsets.UTF_8);
//            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//            // Передача файлов HTTP-запрос HTTP-запрос (MultiMaPtace / Form-data)
//            builder.addBinaryBody("image[]", senderFile, ContentType.MULTIPART_FORM_DATA, senderFile.getName());// поток файла
//            // Байт Передача http Запрос головы (приложение / json)
//            ContentType contentType = ContentType.create("application/json", Charsets.UTF_8);
//            for (NameValuePair param : params) {
//                // Передача байтового потока
//                builder.addTextBody(param.getName(), param.getValue(), contentType);
//            }
//            org.apache.http.HttpEntity entity = builder.build();
//            httpPost.setEntity(entity);
//            HttpResponse response = httpClient.execute(httpPost);
//            // выполнить представление
//            org.apache.http.HttpEntity responseEntity = response.getEntity();
//            if (responseEntity != null) {
//                // Конвертировать содержимое ответа на строку
//                return EntityUtils.toString(responseEntity, Charsets.UTF_8);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
//        return "gkj[f";
//    }
    /** Из контроллера */
//    @PostMapping("/sendMessage")
//    public String sendMessage(@ModelAttribute MessageFrom message, Model model,
//                                    @RequestParam(name = "deliveryMethod") Integer deliveryMethod, @RequestParam(name = "sendBodyMethod") Integer sendBodyMethod){
//        model.addAttribute("messageFrom", new MessageFrom());
//        ldapPersonService.initSendMail(model);
//        message.setDeliveryMethod(deliveryMethod);
//        message.setSendBodyMethod(sendBodyMethod);
//        System.out.println(message.getFile().getOriginalFilename());
////        System.out.println(message.toString());
////        ldapPersonService.sendMessage(message, " ");
////        System.out.println(ldapPersonService.sendMessage2(message));
//        ldapPersonService.sendMessageToSagalov(model, message);
////        System.out.println(ldapPersonService.sendMessageToSagalov(message));
//        return "sendMessage";
//    }

    //    @GetMapping("addPersonToMailListUtil")
//    public String addPersonToMailListUtil(Model model, @RequestParam(required = false, name = "nameCompany")String nameCompany,
//                                                @RequestParam(required = false) Integer listId){
//        System.out.println(nameCompany);
//        System.out.println("id: " + listId);
//        if(model.getAttribute("mailsList") == null){
//            ldapPersonService.getAllMaillists(model);
//        }
////        if(listId != null){
////            ldapPersonService.getEmployeesOffTheMailslist(nameCompany, listId);
////            return "addPersonToMailList";
////        }
//        System.out.println("вывел");
//        ldapPersonService.getAllPersonWithEmail(model, nameCompany);
//        return "addPersonToMailList";
//    }

//    @GetMapping("addPersonToMailListUtil")
//    public ModelAndView addPersonToMailListUtil(Model model, @RequestParam(required = false, name = "nameCompany", defaultValue = "ou=bfa development,ou=bfa users,dc=bfad,dc=ru")String nameCompany){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("addPersonToMailList");
//        modelAndView.addObject("personsFromAD", ldapPersonService.getAllPersonWithEmail(model, nameCompany));
//        if(model.getAttribute("mailsList") == null){
//            ldapPersonService.getAllMaillists(model);
//        }
////        System.out.println(nameCompany);
////        ldapPersonService.getAllPersonWithEmail(model, nameCompany);
////        List<PersonsForAdd> list = (List<PersonsForAdd>)model.getAttribute("personsFromAD");
////        System.out.println("оппа");
////        System.out.println(list.toString());
//        System.out.println("вернул");
//        return modelAndView;
//    }
}
