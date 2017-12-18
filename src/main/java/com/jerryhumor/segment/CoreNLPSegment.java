package com.jerryhumor.segment;

import edu.stanford.nlp.ie.crf.CRFClassifier;

import java.util.Date;
import java.util.Properties;

public class CoreNLPSegment {

    private static CoreNLPSegment instance;
    private CRFClassifier classifier;

    private CoreNLPSegment(){
        Properties props = new Properties();
        props.setProperty("sighanCorporaDict", "data");
        props.setProperty("serDictionary", "data/dict-chris6.ser.gz");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        classifier = new CRFClassifier(props);
        classifier.loadClassifierNoExceptions("data/ctb.gz", props);
        classifier.flags.setProperties(props);
    }

    public static CoreNLPSegment getInstance() {
        if (instance == null) {
            instance = new CoreNLPSegment();
        }

        return instance;
    }

    public static void init(){
        instance = new CoreNLPSegment();
    }

    public String[] doSegment(String data) {
        return (String[]) classifier.segmentString(data).toArray();
    }

    public static void main(String[] args) {

        String sentence = "他和我在学校里常打桌球。";
        String ret[] = CoreNLPSegment.getInstance().doSegment(sentence);
        for (String str : ret) {
            System.out.println(str);
        }
        System.out.println(new Date().getTime());
        String example = "饮食习惯的差异不仅存在于国与国之间，一国南北方的饮食差异也是很大的。今天就来说说南北方特色早餐，看看你比较喜欢哪一种！北方人是喜好面食的，所以早餐大多以面食为主，但是面食有些普通，今天就来说些有代表性的。比如西安的羊肉泡馍，在冬日的早晨来一碗暖暖的羊肉泡馍，真是一天都干劲十足啊！而馍就是北方人喜欢的面食。泡在羊肉汤里更是有滋有味。";
        String ret1[] = CoreNLPSegment.getInstance().doSegment(example);
        for (String str : ret1){
            System.out.println(str);
        }
        System.out.println(new Date().getTime());

    }

}