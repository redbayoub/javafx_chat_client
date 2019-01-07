package chatapp.classes.model;



public class FriendRequest {

        private int id;

        private int senderId;

        private int receiverId;

        public FriendRequest() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }

        public int getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(int receiverId) {
            this.receiverId = receiverId;
        }


        @Override
        public String toString() {
            return "FriendRequest{" +
                    "id=" + id +
                    ", senderId=" + senderId +
                    ", receiverId=" + receiverId +
                    '}';
        }


}
