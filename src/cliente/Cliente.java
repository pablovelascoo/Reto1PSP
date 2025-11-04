package cliente;

import java.awt.EventQueue;
import view.ClienteView;

public class Cliente {
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ClienteView frame = new ClienteView();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}