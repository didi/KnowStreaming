export enum ConfigOperate {
  Add,
  Edit,
}

export type ConfigProps = {
  id?: number;
  valueGroup?: string;
  valueName?: string;
  value?: string;
  status?: 0 | 1;
  operator?: string;
  memo?: string;
};

export type AddConfigProps = Omit<ConfigProps, 'id' | 'operator'>;
export type EditConfigProps = Omit<ConfigProps, 'operator'>;
