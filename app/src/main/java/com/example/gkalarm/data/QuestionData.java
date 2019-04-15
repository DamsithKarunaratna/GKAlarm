package com.example.gkalarm.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionData {

    private static List<Question> questionsList = new ArrayList<>();

    public static Question getRandomQuestion() {
        Random rand = new Random();
        return questionsList.get(rand.nextInt(questionsList.size()));
    }


    static {
        questionsList.add(new Question(
                "Entomology is the science that studies",
                "Behavior of human beings",
                "Insects",
                "The origin and history of technical and scientific terms",
                "The formation of rocks",
                2
                ));
        questionsList.add(new Question(
                "Hitler party which came into power in 1933 is known as",
                "Labour Party",
                "Ku-Klux-Klan",
                "Nazi Party",
                "Democratic Party",
                3
        ));
        questionsList.add(new Question(
                "Exposure to sunlight helps a person improve his health because",
                "the infrared light kills bacteria in the body",
                "resistance power increases",
                "the pigment cells in the skin get stimulated and produce a healthy tan",
                "the ultraviolet rays convert skin oil into Vitamin D",
                4
        ));
        questionsList.add(new Question(
                "Golf player Vijay Singh belongs to which country?",
                "USA",
                "Fiji",
                "India",
                "Pakistan",
                2
        ));
        questionsList.add(new Question(
                "\t\n" +
                        "For safety, the fuse wire used in the mains for household supply of " +
                        "electricity must be made of metal having",
                "low melting point",
                "high resistance",
                "high melting point",
                "low specific heat",
                1
        ));

    }

    public static class Question {
        private String question;
        private String option1;
        private String option2;
        private String option3;
        private String option4;

        private int answr;

        public Question(String question,
                            String option1, String option2, String option3, String option4, int answr) {
            this.question = question;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.answr = answr;
        }

        public Question() {
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getOption1() {
            return option1;
        }

        public void setOption1(String option1) {
            this.option1 = option1;
        }

        public String getOption2() {
            return option2;
        }

        public void setOption2(String option2) {
            this.option2 = option2;
        }

        public String getOption3() {
            return option3;
        }

        public void setOption3(String option3) {
            this.option3 = option3;
        }

        public String getOption4() {
            return option4;
        }

        public void setOption4(String option4) {
            this.option4 = option4;
        }

        public int getAnswr() {
            return answr;
        }

        public void setAnswr(int answr) {
            this.answr = answr;
        }
    }
}
