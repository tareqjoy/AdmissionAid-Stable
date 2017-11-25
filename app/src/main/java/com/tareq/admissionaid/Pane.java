package com.tareq.admissionaid;



public class Pane {

    public String VersityName = "", Title = "", Info = "", Time = "", Link = "", FileName="", IntentName="";
    public Boolean Downloadable=false;
    private StringBuilder MakeString;

    public Pane(Pane p){
        this.VersityName = p.VersityName;
        this.Title = p.Title;
        this.Info = p.Info;
        this.Time = p.Time;
        this.Link = p.Link;
        this.FileName = p.FileName;
        this.IntentName = p.IntentName;
        this.Downloadable = p.Downloadable;

    }
    public Pane(String VersityName, String Title, String Info, String Time, String Link){
        this.VersityName = VersityName;
        this.Title = Title;
        this.Info = Info;
        this.Time = Time;
        this.Link = Link;
        UpdateStatus();
    }
    public void UpdateStatus() {

        MakeString = new StringBuilder();

        MakeString.append(VersityName + "_");
        for (int i = 0; i < Title.length(); i++) {
            if ((Title.charAt(i) >= 97 && Title.charAt(i) <= 122) || (Title.charAt(i) >= 65 && Title.charAt(i) <= 90)) {
                MakeString.append(Title.charAt(i));
            }
        }

        FileName += "_";
        for (int i = Link.length() - 1; i >= 0; i--) {
            if ((Link.charAt(i) >= 97 && Link.charAt(i) <= 122) || (Link.charAt(i) >= 65 && Link.charAt(i) <= 90)) {
                MakeString.append(Link.charAt(i));
            }
        }
        if (Link.endsWith(".pdf")) {

            IntentName = "application/pdf";
            Downloadable = true;
            MakeString.append(".pdf");

        } else if (Link.endsWith(".doc")) {

            IntentName = "application/msword";
            Downloadable = true;
            MakeString.append(".doc");

        } else if (Link.endsWith(".docx")) {

            IntentName = "application/msword";
            Downloadable = true;
            MakeString.append(".docx");

        } else if (Link.endsWith(".jpg")) {

            IntentName = "image/jpeg";
            Downloadable = true;
            MakeString.append(".jpg");

        } else if (Link.endsWith(".png")) {

            IntentName = "image/jpeg";
            Downloadable = true;
            MakeString.append(".png");

        } else {

            IntentName = "";
            Downloadable = false;
        }
        FileName = MakeString.toString();

    }
}
