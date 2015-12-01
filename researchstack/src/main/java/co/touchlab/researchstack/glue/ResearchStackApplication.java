package co.touchlab.researchstack.glue;

import android.content.Context;

import com.google.gson.Gson;

import co.touchlab.researchstack.glue.common.Constants;
import co.touchlab.researchstack.glue.common.model.User;
import co.touchlab.researchstack.core.ResearchStackCoreApplication;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.FileAccess;

public abstract class ResearchStackApplication extends ResearchStackCoreApplication
{
    public static final String TEMP_USER_JSON_FILE_NAME = "/temp_user";
    protected static ResearchStackApplication instance;

    private User currentUser;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    //TODO Thread safe
    public static ResearchStackApplication getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException("Accessing instance of application before onCreate");
        }

        return instance;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // File Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getStudyOverviewResourceId();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getLargeLogoDiseaseIcon();

    public abstract int getConsentForm();

    public abstract int getConsentSections();

    public abstract int getQuizSections();

    public abstract int getLearnSections();

    public abstract int getPrivacyPolicy();

    public abstract int getLicenseSections();

    public String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    public abstract int getAppName();

    public String getHTMLFilePath(String docName)
    {
        return getRawFilePath(docName,
                "html");
    }

    public String getPDFFilePath(String docName)
    {
        return getRawFilePath(docName,
                "pdf");
    }


    public String getRawFilePath(String docName, String postfix)
    {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public int getDrawableResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public abstract boolean isSignatureEnabledInConsent();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Other (unorganized)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
    public abstract Constants.UserInfoType[] getUserInfoTypes();

    public abstract Class getInclusionCriteriaSceneClass();

    //TODO: The whole user thing needs to change.  To discuss.
    public User getCurrentUser()
    {
        loadUser();
        return currentUser;
    }

    public boolean storedUserExists()
    {
        return getFileAccess().dataExists(this, TEMP_USER_JSON_FILE_NAME);
    }

    public void saveUser()
    {
        Gson gson = new Gson();
        String userJsonString = gson.toJson(getCurrentUser());

        LogExt.d(getClass(),
                "Writing user json:\n" + userJsonString);

        getFileAccess().writeString(this, TEMP_USER_JSON_FILE_NAME, userJsonString);
    }

    public void loadUser()
    {
        Gson gson = new Gson();
        FileAccess fileAccess = getFileAccess();

        if (fileAccess.dataExists(this, TEMP_USER_JSON_FILE_NAME))
        {
            String jsonString = fileAccess.readString(this, TEMP_USER_JSON_FILE_NAME);
            currentUser = gson.fromJson(jsonString, User.class);
        }

        if (currentUser == null)
        {
            LogExt.d(getClass(),
                    "No user file found, creating new user");
            currentUser = new User();
        }
    }

    public void clearUserData(Context context)
    {
        FileAccess fileAccess = getFileAccess();
        if(fileAccess.dataExists(context, TEMP_USER_JSON_FILE_NAME))
        {
            fileAccess.clearData(context, TEMP_USER_JSON_FILE_NAME);
        }
    }
}