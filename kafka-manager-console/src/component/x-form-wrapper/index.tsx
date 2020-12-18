import * as React from 'react';
import { Drawer, Modal, Button, message } from 'component/antd';
import { XFormComponent } from 'component/x-form';
import { IXFormWrapper } from 'types/base-type';
import { wrapper } from 'store';

export class XFormWrapper extends React.Component<IXFormWrapper> {
  public state = {
    confirmLoading: false,
    formMap: this.props.formMap || [] as any,
    formData: this.props.formData || {}
  };

  private $formRef: any;

  public updateFormMap$(formMap?: any, formData?: any, isResetForm?: boolean, resetFields?: string[]) {
    if (isResetForm) {
      resetFields ? this.resetForm(resetFields) : this.resetForm();
    }
    this.setState({
      formMap,
      formData,
    });
  }

  public render() {
    const { type } = this.props;
    switch (type) {
      case 'drawer':
        return this.renderDrawer();
      default:
        return this.renderModal();
    }
  }

  public renderDrawer() {
    const {
      visible,
      title,
      width,
      formData,
      formMap,
      formLayout,
      cancelText,
      okText,
      customRenderElement,
      noform,
      nofooter,
    } = this.props;

    return (
      <Drawer
        title={title}
        visible={visible}
        width={width}
        closable={true}
        onClose={this.handleCancel}
        destroyOnClose={true}
      >
        <>
          {customRenderElement}
        </>
        {!noform && (
          <XFormComponent
            ref={form => this.$formRef = form}
            formData={formData}
            formMap={formMap}
            formLayout={formLayout}
          />)}
        {!nofooter && (<div className="footer-btn">
          <Button type="primary" onClick={this.handleSubmit}>{okText || '确认'}</Button>
          <Button onClick={this.handleCancel}>{cancelText || '取消'}</Button>
        </div>)}
        <>
        </>
      </Drawer>
    );
  }

  public renderModal() {
    const { visible, title, width, formLayout, cancelText, okText, customRenderElement } = this.props;
    const { formMap, formData } = this.state;
    return (
      <Modal
        width={width}
        title={title}
        visible={visible}
        confirmLoading={this.state.confirmLoading}
        maskClosable={false}
        onOk={this.handleSubmit}
        onCancel={this.handleCancel}
        okText={okText || '确认'}
        cancelText={cancelText || '取消'}
      >
        <XFormComponent
          ref={form => this.$formRef = form}
          formData={formData}
          formMap={formMap}
          formLayout={formLayout}
        />
        <>{customRenderElement}</>
      </Modal>
    );
  }

  public handleSubmit = () => {
    this.$formRef.validateFields((error: Error, result: any) => {
      if (error) {
        return;
      }
      const { onSubmit, isWaitting, onSubmitFaild } = this.props;

      if (typeof onSubmit === 'function') {
        if (isWaitting) {
          this.setState({
            confirmLoading: true,
          });
          onSubmit(result).then(() => {
            message.success('操作成功');
            this.resetForm();
            this.closeModalWrapper();
          }).catch((err: any) => {
            const { formMap, formData } = wrapper.xFormWrapper;
            onSubmitFaild(err, this.$formRef, formData, formMap);
          }).finally(() => {
            this.setState({
              confirmLoading: false,
            });
          });
          return;
        }

        // tslint:disable-next-line:no-unused-expression
        onSubmit && onSubmit(result);

        this.resetForm();
        this.closeModalWrapper();
      }
    });
  }

  public handleCancel = () => {
    const { onCancel } = this.props;
    // tslint:disable-next-line:no-unused-expression
    onCancel && onCancel();
    this.resetForm();
    this.closeModalWrapper();
  }

  public resetForm = (resetFields?: string[]) => {
    // tslint:disable-next-line:no-unused-expression
    this.$formRef && this.$formRef.resetFields(resetFields || '');
  }

  public closeModalWrapper = () => {
    const { onChangeVisible } = this.props;
    // tslint:disable-next-line:no-unused-expression
    onChangeVisible && onChangeVisible(false);
  }
}
