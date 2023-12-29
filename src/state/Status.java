package state;

import Exceptions.CustomException;
import appRun.Task;
import appRun.TaskManager;

public enum Status {
    NEW {
        @Override
        public void changeToIN_PROGRESS(Task task) {
            task.setStatus(Status.IN_PROGRESS);
        }

        @Override
        public void changeToDONE(Task task) throws CustomException {
            throw new CustomException("You cannot make new task as done");
        }

        @Override
        public void changeDescription(Task task, String msg) {
            task.setDescription(msg);
        }

        @Override
        public void delete(Task task, TaskManager todo) {
            todo.deleteTask(task.getTitle());
        }
    },
    IN_PROGRESS {
        @Override
        public void changeToIN_PROGRESS(Task task) throws CustomException {
            throw new CustomException("The task is already in progress");
        }

        @Override
        public void changeToDONE(Task task) {
            task.setStatus(Status.DONE);
        }

        @Override
        public void changeDescription(Task task, String msg) throws CustomException {
            throw new CustomException("You cannot change description in task which is already in progress");
        }

        @Override
        public void delete(Task task, TaskManager todo) throws CustomException {
            throw new CustomException("You cannot delete task which is already in progress");
        }
    },
    DONE {
        @Override
        public void changeToIN_PROGRESS(Task task) throws CustomException {
            throw new CustomException("Task has already been done");
        }

        @Override
        public void changeToDONE(Task task) throws CustomException {
            throw new CustomException("Task has already been done");
        }

        @Override
        public void changeDescription(Task task, String msg) throws CustomException {
            throw new CustomException("You cannot change description in task which has been done");
        }

        @Override
        public void delete(Task task, TaskManager todo) throws CustomException {
            throw new CustomException("You cannot delete task which has been done");
        }
    };

    public abstract void changeToIN_PROGRESS (Task task) throws CustomException;

    public abstract void changeToDONE(Task task) throws CustomException;

    public abstract void changeDescription(Task task, String msg) throws CustomException;
    public abstract void delete(Task task, TaskManager todo) throws CustomException;

}
