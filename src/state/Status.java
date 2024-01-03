package state;

import Exceptions.CustomException;
import appRun.Task;
import appRun.TaskManager;

public enum Status {
    NEW {
        @Override
        public Status changeToIN_PROGRESS(Task task) {
            task.setStatus(Status.IN_PROGRESS);
            return IN_PROGRESS;
        }

        @Override
        public Status changeToDONE(Task task) throws CustomException {
            throw new CustomException("\u001B[31m"+"You cannot make new task as done"+"\u001B[0m");
        }

        @Override
        public void changeDescription(Task task, String msg) {
            task.setDescription(msg);
        }

    },
    IN_PROGRESS {
        @Override
        public Status changeToIN_PROGRESS(Task task) throws CustomException {
            throw new CustomException(TaskManager.RED+"The task is already in progress"+TaskManager.RESET);
        }

        @Override
        public Status changeToDONE(Task task) {
            task.setStatus(Status.DONE);
            return DONE;
        }

        @Override
        public void changeDescription(Task task, String msg) throws CustomException {
            throw new CustomException(TaskManager.RED+"You cannot change description in task which is already in progress"+TaskManager.RESET);
        }
    },
    DONE {
        @Override
        public Status changeToIN_PROGRESS(Task task) throws CustomException {
            throw new CustomException(TaskManager.RED+"Task has already been done"+TaskManager.RESET);
        }

        @Override
        public Status changeToDONE(Task task) throws CustomException {
            throw new CustomException(TaskManager.RED+"Task has already been done"+TaskManager.RESET);
        }

        @Override
        public void changeDescription(Task task, String msg) throws CustomException {
            throw new CustomException(TaskManager.RED+"You cannot change description in task which has been done"+TaskManager.RESET);
        }
    };

    public abstract Status changeToIN_PROGRESS (Task task) throws CustomException;

    public abstract Status changeToDONE(Task task) throws CustomException;

    public abstract void changeDescription(Task task, String msg) throws CustomException;

}
