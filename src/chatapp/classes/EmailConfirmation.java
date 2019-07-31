package chatapp.classes;

import java.util.Random;

public class EmailConfirmation {
    private static EmailConfirmation instance;
    private String email;
    private int generated_nb;
    private EmailConfirmation(String email){
        this.email=email;
        generated_nb=generate_confirm_nb();
    }

    public static EmailConfirmation newInstance(String email) {
        instance=new EmailConfirmation(email);
        return instance;
    }

    public static EmailConfirmation getInstance() {
        return instance;
    }

    public static void removeInstance(){
        instance=null;
    }

    public void send_email_confirmation(){
        //TODO send email confirmation to email
        System.out.println("EmailConfirmation{" +
                "email='" + email + '\'' +
                ", generated_nb=" + generated_nb +
                '}');
    }

    public boolean confirm_email(int confirm_nb){
       return (this.generated_nb==confirm_nb);
    }

    private int generate_confirm_nb(){
        Random random=new Random(System.nanoTime());
        int conf_nb=0;
        for(int i=0;i<6;i++){
            conf_nb+=(conf_nb*10)+random.nextInt(9);
        }
        return conf_nb;
    }


}
