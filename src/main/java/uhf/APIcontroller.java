package uhf;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;


import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


@Controller
@RestController
@Slf4j
@CrossOrigin("*")
@EnableWebMvc
public class APIcontroller {
    public static int connectionstatus = 1;
    public static int Port;
    public static byte[] comAddr = new byte[1];
    public static int[] PortHandle = new int[1];
    public static byte[] Ant = new byte[1];


    public static List<String> Tags = new ArrayList<String>();

    public static int netPort = 27011;
    public static String netIpAddr = "192.168.0.250";

    public static boolean isRunning = false;
    public static boolean isStarted = false;
    public static boolean scannerReady = false;

    @RequestMapping("/stop-inventory")
    public static String stopInventory() {
        MainApp.reader.CloseSpecComPort(PortHandle[0]);
        isRunning = false;
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            return e.toString();
        }
        return Tags.toString();
    }


    public void greeting() {

    }

    public static SseEmitter sseEmitter;


    @Autowired
    public static EmitterService emitterService = new EmitterService();


    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        log.info("subscribing...");
        sseEmitter = new SseEmitter(24 * 60 * 60 * 1000l);
        emitterService.addEmitter(sseEmitter);
        log.info("subscribed...");
        return sseEmitter;
    }


    @RequestMapping("/start-inventory")
    public static String startInventory() {
//        SocketHandler handler = new SocketHandler().handleTextMessage(1, "This is it");

        if (scannerReady) {
            Tags.clear();
        } else {
            new Thread(inventoryTask).start();
        }
        isRunning = true;
        return "Inventory has been started!...";
    }

    public static Runnable inventoryTask = new Runnable() {
        @Override
        public void run() {
            int result = 1000;
            byte beep = 0x0;
            byte baud =5;
            try {
                MainApp.reader.SetBeepNotification(comAddr, beep, PortHandle[0]);
//                result = MainApp.reader.OpenNetPort(netPort, netIpAddr, comAddr, PortHandle);
                result = MainApp.reader.OpenComPort(3, comAddr, baud, PortHandle);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
            try {
                if (result == 0) {
                    isStarted = true;
                    scannerReady = true;
                    byte[] versionInfo = new byte[2];
                    byte[] readerType = new byte[1];
                    byte[] trType = new byte[1];
                    byte[] dmaxfre = new byte[1];
                    byte[] dminfre = new byte[1];
                    byte[] powerdBm = new byte[1];
                    byte[] InventoryScanTime = new byte[1];
                    byte[] Ant = new byte[1];
                    byte[] BeepEn = new byte[1];
                    byte[] OutputRep = new byte[1];
                    byte[] CheckAnt = new byte[1];
                    result = MainApp.reader.GetReaderInformation(comAddr, versionInfo, readerType, trType, dmaxfre, dminfre, powerdBm, InventoryScanTime,
                            Ant, BeepEn, OutputRep, CheckAnt, PortHandle[0]);
                    byte ComAdrData = 0;
                    result = MainApp.reader.SetAddress(comAddr, ComAdrData, PortHandle[0]);
                    byte QValue = 4;
                    byte Session = 0;
                    byte MaskMem = 2;
                    byte[] MaskAdr = new byte[2];
                    byte MaskLen = 0;
                    byte[] MaskData = new byte[256];
                    byte MaskFlag = 0;
                    byte AdrTID = 0;
                    byte LenTID = 6;
                    byte TIDFlag = 1;//��TID��ǰ6����
                    byte Target = 0;
                    byte InAnt = (byte) 0x80;
                    byte Scantime = 0;
                    byte FastFlag = 0;
                    byte[] pEPCList = new byte[20000];
                    int[] Totallen = new int[1];
                    int[] CardNum = new int[1];

                    while (isRunning) {
                        result = MainApp.reader.Inventory_G2(comAddr, QValue, Session, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag,
                                AdrTID, LenTID, TIDFlag, Target, InAnt, Scantime, FastFlag, pEPCList, Ant, Totallen,
                                CardNum, PortHandle[0]);
                        if (CardNum[0] > 0) {
                            int m = 0;
                            for (int index = 0; index < CardNum[0]; index++) {
                                int epclen = pEPCList[m++] & 255;
                                String EPCstr = "";
                                byte[] epc = new byte[epclen];
                                for (int n = 0; n < epclen; n++) {
                                    byte bbt = pEPCList[m++];
                                    epc[n] = bbt;
                                    String hex = Integer.toHexString(bbt & 255);
                                    if (hex.length() == 1) {
                                        hex = "0" + hex;
                                    }
                                    EPCstr += hex;
                                }
                                int rssi = pEPCList[m++];
                                String myEpc = EPCstr.toString();
//                                emitterService.pushNotification(myEpc);
                                sseEmitter.send(myEpc);
                                if (Tags.contains(myEpc)) {
                                    System.out.println(myEpc);
                                } else {
                                    try {
                                        emitterService.pushNotification(myEpc);
                                    } catch (Exception e) {
                                        System.out.println("failed");
                                    }
                                    Tags.add(myEpc);
                                }
                            }
                        }
                    }

                }
            } catch (Exception e) {
                MainApp.reader.CloseNetPort(PortHandle[0]);
                e.toString();
            }
        }
    };

}





















