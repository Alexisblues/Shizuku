package moe.shizuku.server.api;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.ServiceManager;

import moe.shizuku.server.utils.BinderSingleton;

public class Api {

    public static final BinderSingleton<IActivityManager> ACTIVITY_MANAGER_SINGLETON = new BinderSingleton<IActivityManager>() {

        @Override
        protected IActivityManager create() {
            //noinspection deprecation
            return ActivityManagerNative.getDefault();
        }
    };

    public static final BinderSingleton<IPackageManager> PACKAGE_MANAGER_SINGLETON = new BinderSingleton<IPackageManager>() {

        @Override
        protected IPackageManager create() {
            return IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }
    };

    public static final BinderSingleton<IUserManager> USER_MANAGER_SINGLETON = new BinderSingleton<IUserManager>() {

        @Override
        protected IUserManager create() {
            return IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        }
    };

    public static int checkPermission(String permission, int pid, int uid) throws RemoteException {
        IActivityManager am = ACTIVITY_MANAGER_SINGLETON.get();
        if (am == null) {
            throw new RemoteException("can't get IActivityManager");
        }
        return am.checkPermission(permission, pid, uid);
    }

    public static PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        IPackageManager pm = PACKAGE_MANAGER_SINGLETON.get();
        if (pm == null) {
            throw new RemoteException("can't get IPackageManager");
        }
        return pm.getPackageInfo(packageName, flags, userId);
    }
}