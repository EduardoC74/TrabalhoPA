package pt.isec.pa.apoio_poe.ui.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import pt.isec.pa.apoio_poe.model.SistemaManager;
import pt.isec.pa.apoio_poe.model.fsm.SistemState;

import java.util.*;

public class AssignmentProposalsLOCKUI extends BorderPane {
    SistemaManager sistemaManager;
    Button btns1, btns2, btns3, btns4, btns5;
    Label lbEstado, lb1, lb2, lb3, lb4;

    public AssignmentProposalsLOCKUI(SistemaManager sistemaManager) {
        this.sistemaManager = sistemaManager;

        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        lbEstado = new Label("Assignment Proposals LOCK");
        lbEstado.setPadding(new Insets(15));
        setAlignment(lbEstado, Pos.TOP_CENTER);

        lbEstado.setTranslateY(80);
        this.setTop(lbEstado);

        btns1 = new Button("Students list");
        btns1.setMinWidth(250);
        btns2 = new Button("Proposals list");
        btns2.setMinWidth(250);
        btns3 = new Button("Next Phase");
        btns3.setMinWidth(250);
        btns4 = new Button("Previous Phase");
        btns4.setMinWidth(250);
        btns5 = new Button("Quit");
        btns5.setMinWidth(250);

        VBox vBox = new VBox(btns1, btns2, btns3, btns4, btns5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        this.setCenter(vBox);

        lb1 = new Label("Students: ");
        lb1.setId("labeldraw");
        lb2  = new Label("W\\ proposal: ");
        lb2.setId("labeldraw");
        lb3 = new Label("Proposals: ");
        lb3.setId("labeldraw");
        lb4  = new Label("W\\ student: ");
        lb4.setId("labeldraw");

        HBox hBox2 = new HBox(lb1, lb2, lb3, lb4);
        hBox2.setAlignment(Pos.CENTER);
        this.setBottom(hBox2);
        hBox2.setSpacing(30);
        hBox2.setTranslateY(-20);
    }

    private void registerHandlers() {
        sistemaManager.addPropertyChangeListener(evt -> { update(); });
        btns1.setOnAction( event -> {
            List<String> choices = new ArrayList<>();
            choices.add("With self-proposal");
            choices.add("With application already registered");
            choices.add("With proposal assigned");
            choices.add("Without proposal assigned");

            ChoiceDialog<String> dialog = new ChoiceDialog<>("With self-proposal", choices);
            dialog.setTitle("Assignment Proposals");
            dialog.setHeaderText("Students lists");
            dialog.setContentText("Option:");

            // Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();

            if(result.isPresent()) {

                final Popup popup = new Popup();
                popup.setAutoHide(true); //quando dou um toque fora da janela ela desaparece

                ListView listView = new ListView();
                switch (result.get()) {
                    case "With self-proposal" -> listView.getItems().addAll(sistemaManager.consultSelfProposedStudents());
                    case "With application already registered" -> listView.getItems().addAll(sistemaManager.CSWithAlreadyRegisteredApplication());
                    case "With proposal assigned" -> listView.getItems().addAll(sistemaManager.CSwithProposalsAssigned());
                    case "Without proposal assigned" -> listView.getItems().addAll(sistemaManager.CSwithoutProposalsAssigned());
                }

                BorderPane pane = new BorderPane(listView);
                pane.setPadding(new Insets(10));
                pane.setBackground(new Background(
                        new BackgroundFill(Color.WHITESMOKE,
                                new CornerRadii(5), Insets.EMPTY)) //cantos arredondados
                );
                popup.getContent().add(pane);
                popup.show(getScene().getWindow());
                //popup.show(getScene().getWindow(),x+w/2-tableView.getWidth()/2.0*tableView.getWidth(),y+0.80*h); //mostrar popup
                Timer timer = new Timer(true);  //crio um timer
                TimerTask task = new TimerTask() { //esta tarefa vai fazer o hide
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            popup.hide();
                        }); //platform.runLater para quando temos threads e queremos configurar sem ser a principal
                    }
                };
                timer.schedule(task, 30000); //desaparece em 30 segundos
            }
        });
        btns2.setOnAction( event -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Assignment Proposals");
            dialog.setHeaderText("Proposals list");

            // Set the button types.
            ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            CheckBox checkBox1 = new CheckBox();
            CheckBox checkBox2 = new CheckBox();
            CheckBox checkBox3 = new CheckBox();
            CheckBox checkBox4 = new CheckBox();

            grid.add(new Label("Student self-proposals"), 0, 1);
            grid.add(checkBox1, 1, 1);
            grid.add(new Label("Teachers proposals"), 0, 2);
            grid.add(checkBox2, 1, 2);
            grid.add(new Label("Available proposals"), 0, 3);
            grid.add(checkBox3, 1, 3);
            grid.add(new Label("Assigned proposals"), 0, 4);
            grid.add(checkBox4, 1, 4);

            // Enable/Disable login button depending on whether a username was entered.
            Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
            okButton.setDisable(true);

            checkBox1.selectedProperty().addListener((observable, oldValue, newValue) -> {
                okButton.setDisable(!checkBox1.isSelected());
            });
            checkBox2.selectedProperty().addListener((observable, oldValue, newValue) -> {
                okButton.setDisable(!checkBox2.isSelected());
            });
            checkBox3.selectedProperty().addListener((observable, oldValue, newValue) -> {
                okButton.setDisable(!checkBox3.isSelected());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> checkBox1.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {

                    final Popup popup = new Popup();
                    popup.setAutoHide(true); //quando dou um toque fora da janela ela desaparece

                    ListView listView = new ListView();

                    if (checkBox1.isSelected()) {
                        listView.getItems().add(sistemaManager.studentsSelfProposals());
                    }
                    if (checkBox2.isSelected()) {
                        listView.getItems().add(sistemaManager.teachersProposals());
                    }
                    if (checkBox3.isSelected()) {
                        listView.getItems().add(sistemaManager.availableProposals());
                    }
                    if (checkBox4.isSelected()) {
                        listView.getItems().add(sistemaManager.assignedProposals());
                    }

                    BorderPane pane = new BorderPane(listView);
                    pane.setPadding(new Insets(10));
                    pane.setBackground(new Background(
                            new BackgroundFill(Color.WHITESMOKE,
                                    new CornerRadii(5), Insets.EMPTY)) //cantos arredondados
                    );
                    popup.getContent().add(pane);
                    popup.show(getScene().getWindow());
                    //popup.show(getScene().getWindow(),x+w/2-tableView.getWidth()/2.0*tableView.getWidth(),y+0.80*h); //mostrar popup
                    Timer timer = new Timer(true);  //crio um timer
                    TimerTask task = new TimerTask() { //esta tarefa vai fazer o hide
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                popup.hide();
                            }); //platform.runLater para quando temos threads e queremos configurar sem ser a principal
                        }
                    };
                    timer.schedule(task, 30000); //desaparece em 30 segundos

                }

                return null;
            });

            dialog.showAndWait();
        });
        btns3.setOnAction( event -> {
            sistemaManager.up();
        });
        btns4.setOnAction(actionEvent -> {
            sistemaManager.down();
        });
        btns5.setOnAction(actionEvent -> {
            Platform.exit();
        });

    }

    private void update() {
        if (sistemaManager.getState() != SistemState.ASSIGNMENT_PROPOSALS_LOCK) {
            this.setVisible(false);
            return;
        }
        this.setVisible(true);

        lb1.setText("Students: " + sistemaManager.getStudents().size());
        lb2.setText("W\\ proposal: " + sistemaManager.getMapStudentProposal().keySet().size());
        lb3.setText("Proposals: " + sistemaManager.getProposals().size());
        lb4.setText("W\\ student: " + sistemaManager.getMapStudentProposal().values().size());

    }
}
