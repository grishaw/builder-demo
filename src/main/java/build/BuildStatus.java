package build;

/**
 * Created by Grisha Weintraub on 07/01/2017.
 */
public enum BuildStatus{
    ACCEPTED(0),
    QUEUED(1),
    RUNNING(2),
    DONE(3),
    FAILED(4);

    private int id;

    BuildStatus(int id){
        this.id = id;
    }

    public static BuildStatus getById(int id){
        for (BuildStatus s : values()){
            if (s.id == id)
                return s;
        }
        return null;
    }

    public int getId(){
        return id;
    }
}
