package uhf;
import uhf.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.management.NotificationFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmitterService {
    List<SseEmitter> emitters = new ArrayList<>();
    public void addEmitter(SseEmitter emitter){
        emitter.onCompletion(()->emitters.remove(emitter));
        emitter.onTimeout(()->emitters.remove(emitter));
        emitters.add(emitter);
    }
    public void pushNotification(String tag){
        log.info("Pushing tag....");
        List<SseEmitter> deadEmitters= new ArrayList<>();
        Notification payload = Notification
                .builder()
                .tags(tag)
                .build();

        emitters.forEach(emitter -> {
            try{
                emitter.send(SseEmitter
                .event()
                .data(payload));
            }catch (IOException e){
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
