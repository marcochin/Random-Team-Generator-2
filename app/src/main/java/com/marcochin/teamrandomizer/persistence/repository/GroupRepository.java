package com.marcochin.teamrandomizer.persistence.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.marcochin.teamrandomizer.database.GroupDao;
import com.marcochin.teamrandomizer.model.Group;
import com.marcochin.teamrandomizer.ui.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class GroupRepository {
    public static final String SAVE_SUCCESS = "Saved";
    public static final String SAVE_FAILED = "Save failed";
    public static final String UPDATE_SUCCESS = "Updated";
    public static final String UPDATE_FAILED = "Update Failed";
    public static final String DELETE_SUCCESS = "Deleted";
    public static final String DELETE_FAILED = "Delete failed";

    public enum UpdateMessage {
        TYPE_SAVE, TYPE_UPDATE
    }

    // Injected
    private GroupDao mGroupDao;

    @Inject
    public GroupRepository(GroupDao groupDao) {
        mGroupDao = groupDao;
    }

    public LiveData<Resource<Integer>> insertGroup(Group group) {
        return LiveDataReactiveStreams.fromPublisher(
                mGroupDao.insert(group)
                        .map(new Function<Long, Integer>() {
                            @Override
                            public Integer apply(Long aLong) throws Exception {
                                long l = aLong;
                                return (int) l;
                            }
                        })
                        .onErrorReturn(new Function<Throwable, Integer>() {
                            @Override
                            public Integer apply(Throwable throwable) throws Exception {
                                return -1;
                            }
                        })
                        .map(new Function<Integer, Resource<Integer>>() {
                            @Override
                            public Resource<Integer> apply(Integer integer) throws Exception {
                                if (integer > 0) {
                                    return Resource.success(integer, SAVE_SUCCESS);
                                } else {
                                    return Resource.error(null, SAVE_FAILED);
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .toFlowable());
    }

    public LiveData<Resource<Integer>> updateGroup(Group group, final UpdateMessage msgType) {
        return LiveDataReactiveStreams.fromPublisher(
                mGroupDao.update(group)
                        .onErrorReturn(new Function<Throwable, Integer>() {
                            @Override
                            public Integer apply(Throwable throwable) throws Exception {
                                return -1;
                            }
                        })
                        .map(new Function<Integer, Resource<Integer>>() {
                            @Override
                            public Resource<Integer> apply(Integer integer) throws Exception {
                                String message;
                                if (integer > 0) {
                                    if (msgType == UpdateMessage.TYPE_UPDATE) {
                                        message = UPDATE_SUCCESS;
                                    } else {
                                        message = SAVE_SUCCESS;
                                    }
                                    return Resource.success(integer, message);

                                } else {
                                    if (msgType == UpdateMessage.TYPE_UPDATE) {
                                        message = UPDATE_FAILED;
                                    } else {
                                        message = SAVE_FAILED;
                                    }
                                    return Resource.error(integer, message);
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .toFlowable());
    }

    public LiveData<Resource<Integer>> deleteGroup(Group group) {
        return LiveDataReactiveStreams.fromPublisher(
                mGroupDao.delete(group)
                        .onErrorReturn(new Function<Throwable, Integer>() {
                            @Override
                            public Integer apply(Throwable throwable) throws Exception {
                                return -1;
                            }
                        })
                        .map(new Function<Integer, Resource<Integer>>() {
                            @Override
                            public Resource<Integer> apply(Integer integer) throws Exception {
                                if (integer > 0) {
                                    return Resource.success(integer, DELETE_SUCCESS);
                                } else {
                                    return Resource.error(null, DELETE_FAILED);
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .toFlowable());
    }

    public LiveData<Group> getMostRecentGroup() {
        return mGroupDao.getMostRecentGroup();
    }

    public LiveData<Group> getTheNewGroup(){
        return mGroupDao.getTheNewGroup();
    }

    public LiveData<List<Group>> getAllGroups() {
        return mGroupDao.getAllGroups();
    }
}
