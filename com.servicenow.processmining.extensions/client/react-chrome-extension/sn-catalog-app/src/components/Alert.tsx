import { ReactNode } from "react";

interface Props {
    children: ReactNode;
    color?: 'alert-primary' | 'alert-success' | 'alert-danger';
    onClose: () => void;
}

const Alert = ({children, color = 'alert-primary', onClose}: Props) => {
    return (
        <div className={'alert ' + color + ' alert-dismissible fade show'}>{children}
            <button type="button" className="btn-close" onClick={onClose} data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    );
}

export default Alert;