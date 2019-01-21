package Util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class FtpCli {

    FTPClient mFtpClient;
    FTPFile FileList[];
    private String logstr = "";
    private boolean isConnect = false;
    private boolean isLogin	  = false;

    public FtpCli(String host, int port, String usr, String pwd){
        initFTPClient();
        if(mFtpClient != null){
            FTPconnect(host,port);
            FTPLogin(usr,pwd);
            setCharSet("UTF-8");  //默认
        }
    }

    /**
     * 初始化ftp
     */
    public void initFTPClient(){
        this.mFtpClient = new FTPClient();
    }

    /**
     * 连接
     */
    public void FTPconnect(String host, int port){
        try {
            mFtpClient.getConnector().setConnectionTimeout(1500);
            mFtpClient.getConnector().setReadTimeout(1500);
            mFtpClient.getConnector().setCloseTimeout(1500);
            mFtpClient.connect(host, port);
            isConnect = true;
            logstr = logstr
                    + "Connecting to FTP server "
                    + host
                    + " on port "
                    + port;
        } catch (Exception e) {
            isConnect = false;
            logstr = e.getMessage() + "\n";
        }
    }
    /**
     * 登陆
     */
    public void FTPLogin(String usr, String pwd){
        try {
            mFtpClient.login(usr, pwd);
            isLogin = true;
            logstr = logstr
                    + "user "
                    + usr
                    + " login in."
                    + "\n";
        } catch (Exception e) {
            isLogin = false;
            e.printStackTrace();
            logstr = e.getMessage() + "\n";
        }
    }

    /**
     * host
     */
    public String getHost(){
        if(mFtpClient != null)
            return mFtpClient.getHost();
        return null;
    }

    /**
     * 设置字符集
     */
    public void setCharSet(String charset){
        if(isLogin)
            mFtpClient.setCharset(charset);
    }

    /**
     * 注销
     */
    public void logout() {
        if (null != mFtpClient) {
            try {
                mFtpClient.logout();
                mFtpClient.disconnect(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取错误报告
     */
    public String getErrLog(){
        return logstr;
    }

    /**
     * 获取实例
     */
    public FTPClient getFTPInstance(){
        return mFtpClient;
    }

    /**
     * 获取连接状态
     */
    public boolean getIsConnect(){
        return isConnect;
    }

    /**
     * 获取登陆状态
     */
    public boolean getIsLogin(){
        return isLogin;
    }

    /**
     * 获取列表
     * */
    public void FtpFileList(String path) {
        try {
            if (path != "" && path != null) {
                mFtpClient.changeDirectory(path);
            }
            FileList = mFtpClient.list();
            FileList = sortFile(FileList); //排序
        } catch (Exception e) {
            FileList = null;
            e.printStackTrace();
        }
    }

    public FTPFile[] getFileList(String path){
        try {
            if (path != "" && path != null) {
                mFtpClient.changeDirectory(path);
                return mFtpClient.list();
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 上一级目录
     */
    public void parentFtpFileList() {
        try {
            mFtpClient.changeDirectoryUp();
            FileList = mFtpClient.list();
            FileList = sortFile(FileList); //排序
        } catch (Exception e) {
            FileList = null;
            e.printStackTrace();
        }
    }

    /**
     * 得到当前路径
     */
    public String getCurPath(){
        String str = "";
        if(isLogin){
            try {
                str =  mFtpClient.currentDirectory();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * 跳转目录
     */
    public void changeDir(String str){
        if (str != "" && str != null) {
            try {
                mFtpClient.changeDirectory(str);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 上一级目录
     */
    public void changeUpDir(){
        try {
            mFtpClient.changeDirectoryUp();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将文件数组排序，目录放在上面，文件在下面
     * @param file
     * @return
     */
    public FTPFile[] sortFile(FTPFile[] file)  {
        ArrayList<FTPFile> list = new ArrayList<FTPFile>();
        //放入所有目录
        for (FTPFile f : FileList)  {
            if (f.getType() == 1){
                list.add(f);
            }
        }
        //放入所有文件
        for (FTPFile f : FileList)  {
            if (f.getType() == 0){
                list.add(f);
            }
        }

        return list.toArray(new FTPFile[file.length]);
    }

    /**
     * 得到列表方法
     */
    public FTPFile[] getFtpFileList(){
        return FileList;
    }

    /**
     * 重命名 src-->dst
     * @param src
     * @param dst
     */
    public void RenameFTPfile(String src, String dst){
        try {
            mFtpClient.rename(src,dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建文件夹
     */
    public void NewFolder(String dst){
        try {
            mFtpClient.createDirectory(dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除目录
     *
     * @param
     *
     * @param
     *
     * @throws Exception
     */
    public void deleteFolder(String path){
        try {
            mFtpClient.changeDirectory(path);
            FTPFile[] files = mFtpClient.list();
            String name = null;
            for (FTPFile file : files) {
                name = file.getName();
                // 排除隐藏目录
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }
                if (file.getType() == FTPFile.TYPE_DIRECTORY) { // 递归删除子目录
                    deleteFolder(file.getName());
                } else if (file.getType() == FTPFile.TYPE_FILE) { // 删除文件
                    mFtpClient.deleteFile(file.getName());
                }
            }
            mFtpClient.changeDirectoryUp();
            mFtpClient.deleteDirectory(path); // 删除当前目录
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * @param path
     */
    public void deleteFile(String path){
        try {
            mFtpClient.deleteFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 上传
     * path为空时，默认当前路径
     */
    public void FTPupload(File srcfile, String path, FTPDataTransferListener UpLoadListener){
        if (srcfile.exists()) {
            if(path != ""){
                try {
                    mFtpClient.changeDirectory(path);
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FTPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                mFtpClient.upload(srcfile, UpLoadListener);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPDataTransferException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FTPAbortedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载
     */
    public void FTPdownload(String remotepath, String localpath, FTPDataTransferListener DownLoadListener){
        File file = new File(localpath);
        try {
            mFtpClient.download(remotepath, file, DownLoadListener);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPAbortedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
