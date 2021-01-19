package it.bz.odh.spreadsheets.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@RestController
//@RequestMapping("/trigger")
public class ChangeNotificationController {

//    private Logger logger = Logger.getLogger(ChangeNotificationController.class);


//    @PostMapping(value = "/notification",headers = { "content-type=text/plain" })
//    @ResponseBody
    public ResponseEntity<String> handleValidation(@RequestParam("validationToken") String validationToken) {
        System.out.println(validationToken);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                .body(StringEscapeUtils.escapeHtml4(validationToken));
    }




//    @PostMapping("/notification")
//    @ResponseBody
    public ResponseEntity<String> handleNotification(@RequestBody() final String jsonString){
        System.out.println(jsonString);
        return ResponseEntity.ok().body("");
    }



}
