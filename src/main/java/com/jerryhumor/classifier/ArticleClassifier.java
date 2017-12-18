package com.jerryhumor.classifier;

import com.jerryhumor.news.constant.NewsType;
import com.jerryhumor.segment.CoreNLPSegment;
import com.jerryhumor.util.Transformer;
import com.jerryhumor.util.TypeUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 文章分类器
 */

public class ArticleClassifier {

    //训练文章地址前缀
    private static final String ADDR_TRAIN_ARTICLES_PREFIX = "src/main/resources/train_article/";
    //无用词文件地址
    private static final String ADDR_WORTHLESS_WORD = "src/main/resources/worthless_words.txt";
    //训练结果 分类信息文件前缀
    private static final String ADDR_CLASSIFIER_DATA_PREFIX = "src/main/resources/classifier_data/";
    
    private static final String ADDR_TEST_ARTICLES_PREFIX = "src/main/resources/test_article/";

    //训练文章名
    private static final String TRAIN_ARTICLE_NAME_ART = "art.txt";
    private static final String TRAIN_ARTICLE_NAME_CAR = "car.txt";
    private static final String TRAIN_ARTICLE_NAME_EDUCATE = "educate.txt";
    private static final String TRAIN_ARTICLE_NAME_FINANCE = "finance.txt";
    private static final String TRAIN_ARTICLE_NAME_FUN = "fun.txt";
    private static final String TRAIN_ARTICLE_NAME_SPORT = "sport.txt";
    private static final String TRAIN_ARTICLE_NAME_TECH = "tech.txt";
    private static final String TRAIN_ARTICLE_NAME_POLITICS = "politics.txt";
    private static final String TRAIN_ARTICLE_NAME_SOCIAL = "social.txt";
    private static final String TRAIN_ARTICLE_NAME_WAR = "war.txt";

    //训练结果 分类信息文件名称
    private static final String CLASSIFIER_DATA_NAME_WORD = "classifier_data_word.txt";
    private static final String CLASSIFIER_DATA_NAME_ART = "classifier_data_art.txt";
    private static final String CLASSIFIER_DATA_NAME_CAR = "classifier_data_car.txt";
    private static final String CLASSIFIER_DATA_NAME_EDUCATE = "classifier_data_educate.txt";
    private static final String CLASSIFIER_DATA_NAME_FINANCE = "classifier_data_finance.txt";
    private static final String CLASSIFIER_DATA_NAME_FUN = "classifier_data_fun.txt";
    private static final String CLASSIFIER_DATA_NAME_SPORT = "classifier_data_sport.txt";
    private static final String CLASSIFIER_DATA_NAME_TECH = "classifier_data_tech.txt";
    private static final String CLASSIFIER_DATA_NAME_POLITICS = "classifier_data_politics.txt";
    private static final String CLASSIFIER_DATA_NAME_SOCIAL = "classifier_data_social.txt";
    private static final String CLASSIFIER_DATA_NAME_WAR = "classifier_data_war.txt";
    private static final String CLASSIFIER_DATA_NAME_TOTAL = "classifier_data_total.txt";

    private static ArticleClassifier mInstance;

    private List<String> mWordList;                                     //要判断的关键词列表
    private List<Double> mTechProbabilityList;                          //科技类型几率列表
    private List<Double> mSportProbabilityList;                         //体育类型几率列表
    private List<Double> mFunProbabilityList;                           //娱乐类型几率列表
    private List<Double> mCarProbabilityList;                           //汽车类型几率列表
    private List<Double> mArtProbabilityList;                           //艺术类型几率列表
    private List<Double> mFinanceProbabilityList;                       //金融类型几率列表
    private List<Double> mEducateProbabilityList;                       //教育类型几率列表
    private List<Double> mWarProbabilityList;                           //军事类型几率列表
    private List<Double> mPoliticsProbabilityList;                      //政治类型几率列表
    private List<Double> mSocialProbabilityList;                        //社会类型几率列表
    private List<Double> mWordProbabilityList;                          //所有词的几率列表

    private List<Article> mArticleList;
    private List<String> mWorthlessWordList;

    private boolean mIsInited = false;

    private CoreNLPSegment mSegment;

    private ArticleClassifier() {
        System.out.println("初始化分类器");
        if (mIsInited){
            System.out.println("分类器已初始化");
            return;
        }


        //导入词类集合
        mWordList = loadWordListFromFile(CLASSIFIER_DATA_NAME_WORD);

        //导入概率文件
        mCarProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_CAR);
        mArtProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_ART);
        mEducateProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_EDUCATE);
        mFinanceProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_FINANCE);
        mFunProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_FUN);
        mSportProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_SPORT);
        mTechProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_TECH);
        mPoliticsProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_POLITICS);
        mWarProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_WAR);
        mSocialProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_SOCIAL);
        mWordProbabilityList = loadProbabilityFromFile(CLASSIFIER_DATA_NAME_TOTAL);

        mSegment = CoreNLPSegment.getInstance();

        mIsInited = true;
        System.out.println("分类器初始化完毕");
    }

    public static ArticleClassifier getInstance() {
        if (mInstance == null){
            mInstance = new ArticleClassifier();
        }
        return mInstance;
    }

    /**
     * 初始化分类器类
     * 获取分类器实例
     */
    public static void init(){
        mInstance = new ArticleClassifier();
    }

    /**
     * 训练文章
     * 生成各类文章的配置文件，供以后分类使用
     *
     * @param trainNum  训练样本的数量 即规定要训练多少篇
     */
    public void train(int trainNum){
        mSegment = CoreNLPSegment.getInstance();
        mArticleList = new ArrayList<Article>();

        //初始化无用词列表
        loadWorthlessWords();

        loadTrainArticle(TRAIN_ARTICLE_NAME_ART, NewsType.CODE_ART, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_CAR, NewsType.CODE_CAR, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_EDUCATE, NewsType.CODE_EDUCATE, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_FINANCE, NewsType.CODE_FINANCE, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_FUN, NewsType.CODE_FUN, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_SPORT, NewsType.CODE_SPORT, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_TECH, NewsType.CODE_TECH, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_POLITICS, NewsType.CODE_POLITICS, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_SOCIAL, NewsType.CODE_SOCIAL, trainNum);

        loadTrainArticle(TRAIN_ARTICLE_NAME_WAR, NewsType.CODE_WAR, trainNum);

        //生成词类集合
        generateWordList();

        //生成频率list
        generateWordFrequency();

        //将词类集合写入文件
        writeWordListToFile(mWordList, CLASSIFIER_DATA_NAME_WORD);

        //将各类文章的概率结果写入文件
        writeFrequencyToFile();
    }

    public void test(){
        init();
        mArticleList = new ArrayList<Article>();
        int trainNum = -1;
        loadTestArticle("art.txt", NewsType.CODE_ART, trainNum);
        loadTestArticle("car.txt", NewsType.CODE_CAR, trainNum);
        loadTestArticle("educate.txt", NewsType.CODE_EDUCATE, trainNum);
        loadTestArticle("finance.txt", NewsType.CODE_FINANCE, trainNum);
        loadTestArticle("fun.txt", NewsType.CODE_FUN, trainNum);
        loadTestArticle("politics.txt", NewsType.CODE_POLITICS, trainNum);
        loadTestArticle("social.txt", NewsType.CODE_SOCIAL, trainNum);
        loadTestArticle("sport.txt", NewsType.CODE_SPORT, trainNum);
        loadTestArticle("tech.txt", NewsType.CODE_TECH, trainNum);
        loadTestArticle("war.txt", NewsType.CODE_WAR, trainNum);
        int correctNum = 0, errorNum = 0;
        for (Article article : mArticleList){
            if (article.getFlag() == classify(article)){
                System.out.println("判断文章类型成功");
                correctNum++;
            }else{
                System.out.println("判断文章类型失败");
                errorNum++;
            }
        }
        System.out.println("正确数：" + correctNum + "，错误数：" + errorNum);

    }

    /**
     * 从文件中导入概率
     * @param fileName
     * @return
     */
    private List<Double> loadProbabilityFromFile(final String fileName){
        long lastTime = new Date().getTime();
        List<Double> probabilityList = new ArrayList<Double>();
        System.out.println("从文件导入概率");
        try{
            File file = new File(ADDR_CLASSIFIER_DATA_PREFIX + fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String probability = null;
            while((probability = reader.readLine()) != null){
                probabilityList.add(Double.parseDouble(probability));
            }
            reader.close();
            System.out.println("导入概率结束，用时" + (new Date().getTime() - lastTime) + "毫秒");
        }catch (Exception e){
            System.out.println("从文件导入概率出错");
            e.printStackTrace();
        }
        return probabilityList;
    }

    /**
     * 从文件中导入词类集合
     * @param fileName
     * @return
     */
    private List<String> loadWordListFromFile(final String fileName){
        long lastTime = new Date().getTime();
        List<String> wordList = new ArrayList<String>();
        System.out.println("从文件导入词集合");
        try{
            File file = new File(ADDR_CLASSIFIER_DATA_PREFIX + fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String word = null;
            while((word = reader.readLine()) != null){
                wordList.add(word);
            }
            reader.close();
            System.out.println("导入词集合结束，用时" + (new Date().getTime() - lastTime) + "毫秒");
        }catch (Exception e){
            System.out.println("从文件导入词集合出错");
            e.printStackTrace();
        }
        return wordList;
    }

    /**
     * 对词类集合过滤无用的词
     * @param wordSet                   需要被过滤的词类集合
     * @param worthlessWordList         需要被滤去的词类列表
     * @return
     */
    private List<String> filterWorthlessWord(HashSet<String> wordSet, List<String> worthlessWordList){
        List<String> filteredWordList = new ArrayList<String>();
        for (String word : wordSet){
            boolean isWorthless = false;
            //去除单字
            if (word.length() <= 1){
                continue;
            }
            //去除数字 包括百分数 小数
            if (TypeUtil.isNumeric(word)){
                continue;
            }
            //去除无用的词
            for (String worthlessWord : worthlessWordList){
                if (worthlessWord.equals(word)){
                    isWorthless = true;
                    break;
                }
            }
            if (!isWorthless){
                filteredWordList.add(word);
            }
        }
        return filteredWordList;
    }

    /**
     * 两个频率列表相加
     * @param frequency1 in
     * @param frequency2 in
     * @return
     */
    private List<Integer> addFrequency(List<Integer> frequency1, final List<Integer> frequency2){
        System.out.println("频率相加，第一个频率个数：" + frequency1.size() + "，第二个频率个数：" + frequency2.size());
        List<Integer> frequencyList = new ArrayList<Integer>();
        if (frequency1.size() == 0){
            return frequency2;
        }else{
            for (int i = 0; i < frequency1.size(); i++){
                frequencyList.add(frequency1.get(i) + frequency2.get(i));
            }
        }
        return frequencyList;
    }

    /**
     * 相对于某一个词类集合，计算该文章的这些词出现的次数，并保存在数组中
     * @param wordList
     * @param article
     * @return
     */
    private List<Integer> convertArticleToFrequencyList(List<String> wordList, Article article){
        List<Integer> frequencyList = new ArrayList<Integer>();
        for (String word : wordList){
            frequencyList.add(calWordFrequencyInArticle(article.getWordList(), word));
        }
        return frequencyList;
    }

    /**
     * 计算当前此在当前词列表中出现的次数
     * @param articleWordList
     * @param word
     * @return
     */
    private int calWordFrequencyInArticle(final List<String> articleWordList, final String word){
        int frequency = 0;
        for (String articleWord : articleWordList){
            if (articleWord.equals(word)){
                frequency ++;
            }
        }
        return frequency;
    }

    /**
     * 统计list中总共有多少词
     * @param wordFrequencyList
     * @return
     */
    private int calWordSum(List<Integer> wordFrequencyList){
        int sum = 0;
        for (int frequency : wordFrequencyList){
            sum += frequency;
        }
        return sum;
    }

    /**
     * 计算概率
     * @param frequencyList
     * @param sum
     * @return
     */
    private List<Double> calProbability(List<Integer> frequencyList, int sum){
        System.out.println("计算概率");
        List<Double> probabilityList = new ArrayList<Double>();
        for (int frequency : frequencyList){
            probabilityList.add(((double)(frequency + 1)) / sum);
        }
        return probabilityList;
    }

    /**
     * 将频率数据写入文件
     * @param frequencyList
     * @param fileName
     */
    private void writeFrequencyToFile(List<Double> frequencyList, String fileName){
        System.out.println("写入概率文件，文件名：" + fileName);
        try{
            FileOutputStream out = out = new FileOutputStream(ADDR_CLASSIFIER_DATA_PREFIX + fileName);
            PrintStream p = new PrintStream(out);
            for (double frequency : frequencyList){
                p.println(frequency);
            }
            p.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将计算出来的概率都写入文件中
     */
    private void writeFrequencyToFile(){
        writeFrequencyToFile(mTechProbabilityList, CLASSIFIER_DATA_NAME_TECH);
        writeFrequencyToFile(mArtProbabilityList, CLASSIFIER_DATA_NAME_ART);
        writeFrequencyToFile(mSportProbabilityList, CLASSIFIER_DATA_NAME_SPORT);
        writeFrequencyToFile(mFinanceProbabilityList, CLASSIFIER_DATA_NAME_FINANCE);
        writeFrequencyToFile(mFunProbabilityList, CLASSIFIER_DATA_NAME_FUN);
        writeFrequencyToFile(mEducateProbabilityList, CLASSIFIER_DATA_NAME_EDUCATE);
        writeFrequencyToFile(mCarProbabilityList, CLASSIFIER_DATA_NAME_CAR);
        writeFrequencyToFile(mPoliticsProbabilityList, CLASSIFIER_DATA_NAME_POLITICS);
        writeFrequencyToFile(mSocialProbabilityList, CLASSIFIER_DATA_NAME_SOCIAL);
        writeFrequencyToFile(mWarProbabilityList, CLASSIFIER_DATA_NAME_WAR);
        writeFrequencyToFile(mWordProbabilityList, CLASSIFIER_DATA_NAME_TOTAL);
    }

    /**
     * 将词集合写入文件
     * @param wordList
     * @param fileName
     */
    private void writeWordListToFile(List<String> wordList, String fileName){
        System.out.println("词类集合写入文件， 文件名：" + fileName);
        try{
            FileOutputStream out = out = new FileOutputStream(ADDR_CLASSIFIER_DATA_PREFIX + fileName);
            PrintStream p = new PrintStream(out);
            for (String word : wordList){
                p.println(word);
            }
            p.close();
            out.close();
        }catch (Exception e){
            System.out.println("词类集合写入文件错误");
            e.printStackTrace();
        }
        System.out.println("词类集合写入文件结束");
    }

    /**
     * 导入
     * @param fileName
     * @param flag
     * @param max           导入文章的数量
     */
    private void loadTrainArticle(String fileName, int flag, int max){
        long lastTime = new Date().getTime();
        System.out.println("导入训练文章, 文章: " + fileName + ", 类型: " + flag + "，数量：" + max);
        if (max == -1){
            max = 65535;
        }
        try{
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(ADDR_TRAIN_ARTICLES_PREFIX + file));
            String content = null;
            int count = 1;
            while((content = reader.readLine()) != null && count <= max){
                System.out.println("正在导入第" + count + "篇文章");
                count++;
                Article article = new Article().setFlag(flag);
                article.setWordList(mSegment.doSegment(content));
                mArticleList.add(article);
            }
            reader.close();
            System.out.println("导入训练文章结束, 耗时: " + (new Date().getTime() - lastTime) + "毫秒");
        }catch (Exception e){
            System.out.println("导入训练文章出错");
            e.printStackTrace();
        }
    }

    /**
     * 导入测试文章
     * @param fileName
     * @param flag
     * @param max
     */
    private void loadTestArticle(String fileName, int flag, int max){
        long lastTime = new Date().getTime();
        System.out.println("导入测试文章, 文章: " + fileName + ", 类型: " + flag + "，数量：" + max);
        if (max == -1){
            max = 65535;
        }
        try{
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(ADDR_TEST_ARTICLES_PREFIX + file));
            String content = null;
            int count = 1;
            while((content = reader.readLine()) != null && count <= max){
                System.out.println("正在导入第" + count + "篇文章");
                count++;
                Article article = new Article().setFlag(flag);
                article.setWordList(mSegment.doSegment(content));
                mArticleList.add(article);
            }
            reader.close();
            System.out.println("导入测试文章结束, 耗时: " + (new Date().getTime() - lastTime) + "毫秒");
        }catch (Exception e){
            System.out.println("导入测试文章出错");
            e.printStackTrace();
        }
    }

    /**
     * 初始化无用次列表
     */
    private void loadWorthlessWords(){
        System.out.println("初始化无用词列表");
        try{
            mWorthlessWordList = new ArrayList<String>();
            File file = new File(ADDR_WORTHLESS_WORD);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String word = null;
            while((word = reader.readLine()) != null){
                mWorthlessWordList.add(word);
            }
            reader.close();
            System.out.println("初始化无用词列表结束");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("初始化无用词列表错误");
        }
    }

    /**
     * 生成词类集合（即属性集合 所有文章的词 不分类）
     */
    private void generateWordList(){
        System.out.println("生成词类集合");
        //生成词类集合
        HashSet<String> wordSet = new HashSet<String>();
        for (Article article : mArticleList){
            wordSet.addAll(article.getWordList());
        }

        //过滤无用的词
        mWordList = filterWorthlessWord(wordSet, mWorthlessWordList);

        System.out.println("生成词类集合结束，共" + mWordList.size() + "个词");
    }

    /**
     * 统计各个类型文章中 词类集合的各个词的频率
     */
    private void generateWordFrequency(){
        System.out.println("生成词概率list");
        System.out.println("共需分析" + mArticleList.size() + "篇文章");

        List<Integer> techFrequencyList = new ArrayList<Integer>();
        List<Integer> sportsFrequencyList = new ArrayList<Integer>();
        List<Integer> funFrequencyList = new ArrayList<Integer>();
        List<Integer> artFrequencyList = new ArrayList<Integer>();
        List<Integer> carFrequencyList = new ArrayList<Integer>();
        List<Integer> financeFrequencyList = new ArrayList<Integer>();
        List<Integer> educateFrequencyList = new ArrayList<Integer>();
        List<Integer> politicsFrequencyList = new ArrayList<Integer>();
        List<Integer> warFrequencyList = new ArrayList<Integer>();
        List<Integer> socialFrequencyList = new ArrayList<Integer>();
        List<Integer> allWordFrequencyList = new ArrayList<Integer>();


        int techWordNum = 0, sportsWordNum = 0, funWordNum = 0, artWordNum = 0,
                carWordNum = 0, financeWordNum = 0, educateWordNum = 0, allWordNum = 0,
                politicsWordNum = 0, warWordNum = 0, socialWordNum = 0;

        int count = 1;
        for (Article article : mArticleList){
            System.out.println("计算第" + count + "篇文章的词频率");
            count++;
            List<Integer> articleVec = convertArticleToFrequencyList(mWordList, article);
            int wordNum = calWordSum(articleVec);
            switch (article.getFlag()){
                case NewsType.CODE_TECH:
                    techFrequencyList = addFrequency(techFrequencyList, articleVec);
                    techWordNum += wordNum;
                    break;
                case NewsType.CODE_SPORT:
                    sportsFrequencyList = addFrequency(sportsFrequencyList, articleVec);
                    sportsWordNum += wordNum;
                    break;
                case NewsType.CODE_FUN:
                    funFrequencyList = addFrequency(funFrequencyList, articleVec);
                    funWordNum += wordNum;
                    break;
                case NewsType.CODE_CAR:
                    carFrequencyList = addFrequency(carFrequencyList, articleVec);
                    carWordNum += wordNum;
                    break;
                case NewsType.CODE_ART:
                    artFrequencyList = addFrequency(artFrequencyList, articleVec);
                    artWordNum += wordNum;
                    break;
                case NewsType.CODE_FINANCE:
                    financeFrequencyList = addFrequency(financeFrequencyList, articleVec);
                    financeWordNum += wordNum;
                    break;
                case NewsType.CODE_EDUCATE:
                    educateFrequencyList = addFrequency(educateFrequencyList, articleVec);
                    educateWordNum += wordNum;
                    break;
                case NewsType.CODE_POLITICS:
                    politicsFrequencyList = addFrequency(politicsFrequencyList, articleVec);
                    politicsWordNum += wordNum;
                    break;
                case NewsType.CODE_WAR:
                    warFrequencyList = addFrequency(warFrequencyList, articleVec);
                    warWordNum += wordNum;
                    break;
                case NewsType.CODE_SOCIAL:
                    socialFrequencyList = addFrequency(socialFrequencyList, articleVec);
                    socialWordNum += wordNum;
                    break;
                default:
                    System.out.println("统计概率，文章类型错误");
                    break;
            }
        }

        System.out.println("统计词频率结束，科技词数：" + techWordNum + "，运动词数：" + sportsWordNum +
                "，娱乐词数：" + funWordNum + "，汽车词数：" + carWordNum +
                "，艺术词数：" + artWordNum + "，金融词数：" + financeWordNum +
                "，教育词数：" + educateWordNum);

        mTechProbabilityList = calProbability(techFrequencyList, techWordNum);
        mSportProbabilityList = calProbability(sportsFrequencyList, sportsWordNum);
        mFunProbabilityList = calProbability(funFrequencyList, funWordNum);
        mCarProbabilityList = calProbability(carFrequencyList, carWordNum);
        mArtProbabilityList = calProbability(artFrequencyList, artWordNum);
        mFinanceProbabilityList = calProbability(financeFrequencyList, financeWordNum);
        mEducateProbabilityList = calProbability(educateFrequencyList, educateWordNum);
        mPoliticsProbabilityList = calProbability(politicsFrequencyList, politicsWordNum);
        mWarProbabilityList = calProbability(warFrequencyList, warWordNum);
        mSocialProbabilityList = calProbability(socialFrequencyList, socialWordNum);

        //统计整体数值
        allWordFrequencyList = addFrequency(allWordFrequencyList, techFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, sportsFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, funFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, carFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, artFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, financeFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, educateFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, politicsFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, socialFrequencyList);
        allWordFrequencyList = addFrequency(allWordFrequencyList, warFrequencyList);

        allWordNum = techWordNum + sportsWordNum + funWordNum + carWordNum + artWordNum + financeWordNum + educateWordNum;
        mWordProbabilityList = calProbability(allWordFrequencyList, allWordNum);
        System.out.println("统计整体频率结束，词数：" + allWordNum);
    }

    /**
     * 计算一篇文章的关于某种类型的概率
     * @param article                       文章
     * @param wordList                      词类集合
     * @param typeProbability               这种类型的 相对于词类集合的概率
     * @param totalProbability              词类集合的总的概率
     * @return
     */
    private double calArticleTypeProbability(Article article, final List<String> wordList, final List<Double> typeProbability, final List<Double> totalProbability){
//        System.out.println("通过Article类计算文章概率");
//        System.out.println("文章词长度：" + article.getWordList().size());
        double probability = 0;
        for (String word : article.getWordList()){
            for (int i = 0; i < wordList.size(); i++){
                if (wordList.get(i).equals(word)){
                    probability += Math.log(typeProbability.get(i));
                    probability -= Math.log(totalProbability.get(i));
                    break;
                }
            }
        }
        return probability;
    }

    /**
     * 对文章进行分类，返回类型值
     * @param article
     * @return
     */
    public int classify(Article article) {
        int category = -1;

        double techProbability = calArticleTypeProbability(article, mWordList, mTechProbabilityList, mWordProbabilityList);
        double funProbability = calArticleTypeProbability(article, mWordList, mFunProbabilityList, mWordProbabilityList);
        double sportsProbability = calArticleTypeProbability(article, mWordList, mSportProbabilityList, mWordProbabilityList);
        double financeProbability = calArticleTypeProbability(article, mWordList, mFinanceProbabilityList, mWordProbabilityList);
        double carProbability = calArticleTypeProbability(article, mWordList, mCarProbabilityList, mWordProbabilityList);
        double artProbability = calArticleTypeProbability(article, mWordList, mArtProbabilityList, mWordProbabilityList);
        double educateProbability = calArticleTypeProbability(article, mWordList, mEducateProbabilityList, mWordProbabilityList);
        double politicsProbability = calArticleTypeProbability(article, mWordList, mPoliticsProbabilityList, mWordProbabilityList);
        double warProbability = calArticleTypeProbability(article, mWordList, mWarProbabilityList, mWordProbabilityList);
        double socialProbability = calArticleTypeProbability(article, mWordList, mSocialProbabilityList, mWordProbabilityList);


        List<Double> probabilityList = new ArrayList<Double>();
        probabilityList.add(techProbability);
        probabilityList.add(funProbability);
        probabilityList.add(sportsProbability);
        probabilityList.add(financeProbability);
        probabilityList.add(carProbability);
        probabilityList.add(artProbability);
        probabilityList.add(educateProbability);
        probabilityList.add(politicsProbability);
        probabilityList.add(socialProbability);
        probabilityList.add(warProbability);

        double maxProbability = techProbability;
        int maxProbabilityIndex = 0;
        for (int i = 0; i < probabilityList.size(); i++){
            if (probabilityList.get(i) > maxProbability){
                maxProbability = probabilityList.get(i);
                maxProbabilityIndex = i;
            }
        }
        switch (maxProbabilityIndex){
            case 0:
                category = NewsType.CODE_TECH;
                break;
            case 1:
                category = NewsType.CODE_FUN;
                break;
            case 2:
                category = NewsType.CODE_SPORT;
                break;
            case 3:
                category = NewsType.CODE_FINANCE;
                break;
            case 4:
                category = NewsType.CODE_CAR;
                break;
            case 5:
                category = NewsType.CODE_ART;
                break;
            case 6:
                category = NewsType.CODE_EDUCATE;
                break;
            case 7:
                category = NewsType.CODE_POLITICS;
                break;
            case 8:
                category = NewsType.CODE_SOCIAL;
                break;
            case 9:
                category = NewsType.CODE_WAR;
                break;
            default: break;
        }

        System.out.println("概率结果：科技：" + techProbability + " 娱乐： " + funProbability +
                " 运动：" + sportsProbability + " 金融：" + financeProbability + " 汽车：" + carProbability +
                " 艺术：" + artProbability + " 教育：" + educateProbability +
                " 政治：" + politicsProbability + " 军事：" + warProbability + " 社会：" + socialProbability);
        System.out.println("判断结果：" + Transformer.getTypeName(category));

        return category;
    }

    /**
     * 对文章进行分类 并返回类型值
     * @param article
     * @return
     */
    public int classify(String article){
        System.out.println(article);
        Article article1 = new Article();
        article1.setWordList(mSegment.doSegment(article));
        return classify(article1);
    }

    public static void main(String[] args){
        ArticleClassifier classifier = ArticleClassifier.getInstance();

        //训练
//        classifier.train(100);

        //测试正确率
        classifier.test();

    }

}
